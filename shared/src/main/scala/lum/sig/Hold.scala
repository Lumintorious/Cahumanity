package lum.sig

import cats.effect.IO
import cats.syntax.functor.*

trait Hold[T] extends SigRoute[T]:
  def set(newValue: T): IO[Unit]
  def get: IO[T]

  def set(newValue: IO[T]): IO[Unit] = newValue.flatMap(set)

object Hold:
  def apply[T](value: => T): Hold[T] =
    HoldImpl[T](value)

  def <~[T](value: => T): Hold[T] =
    HoldImpl[T](value)

  given [T]: Conversion[T, Hold[T]] = Hold(_)

final class HoldImpl[T] private[sig](private var value: T) extends Hold[T]:
  private val freq = new Frequency[T](
    (newValue) => IO { value = newValue }
  )

  def set(newValue: T): IO[Unit] =
    freq.receive(newValue)

  def get: IO[T] =
    IO(value)

  def receive(signal: T): IO[Unit] = freq.receive(signal)
  def subscribe(receive: T => IO[Unit]): IO[Unit] = freq.subscribe(receive) >> set(get)


