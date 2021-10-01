package lum.sig

import collection.mutable.{ Buffer, ListBuffer }
import cats.effect.IO
import cats.Traverse
import scala.concurrent.duration.FiniteDuration
import cats.effect.kernel.Clock
import concurrent.duration.DurationInt


object Frequency:
  def every(duration: FiniteDuration): IO[Frequency[Long]] =
    for
      freq <- IO { Frequency[Long]() }
      dispatch <- IO.sleep(duration)
        .flatMap(_ => Clock[IO].realTime)
        .map(_.toMillis)
        .flatMap(freq.receive)
        .foreverM
        .start
    yield
      freq

class Frequency[T](squiglies: (T => IO[Unit])*) extends SigRoute[T]:
  private val buffer: Buffer[T => IO[Unit]] = squiglies.to(Buffer)

  def subscribe(receive: T => IO[Unit]): IO[Unit] =
    IO { buffer += receive }

  def receive(value: T): IO[Unit] =
    Traverse[List].sequence(buffer.map(_.apply(value)).to(List)).as(())
