package lum.sig

import cats.effect.IO
import cats.Functor
import cats.syntax.functor.toFunctorOps

trait SigSource[+T]:
  def subscribe(receive: T => IO[Unit]): IO[Unit]

  def ~>(target: SigTarget[T]): IO[Unit] =
    subscribe(target.receive)

  def ~>(handler: T => IO[Unit]): IO[Unit] =
    subscribe(handler)

  def ~>(handler: IO[Unit]): IO[Unit] =
    subscribe { _ => handler }

  def map[X](fn: T => X): SigSource[X] = SigSource.Mapped(this, fn)
  def filter(fn: T => Boolean): SigSource[T] = SigSource.Filtered(this, fn)

object SigSource:
  class Mapped[A, B](previous: SigSource[A], map: A => B) extends SigSource[B]:
    def subscribe(receive: B => IO[Unit]): IO[Unit] =
      previous.subscribe(receive compose map)

  class Filtered[T](source: SigSource[T], filter: T => Boolean) extends SigSource[T]:
    def subscribe(receive: T => IO[Unit]): IO[Unit] =
      source.subscribe { value => IO { if filter(value) then receive(value) } }

  extension [F[_]: Functor, E, X](self: SigSource[F[E]])
    def mapDeep(fn: E => X): SigSource[F[X]] =
      self.map(seq => seq.map(fn))
