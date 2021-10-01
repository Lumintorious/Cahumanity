package lum.sig

import cats.effect.IO
import cats.Traverse

trait SigTarget[-T]:
  def receive(signal: T): IO[Unit]

  def receive(signal: IO[T]): IO[Unit] =
    signal.flatMap { sig => receive(sig) }

  def <~ (source: SigSource[T]): IO[Unit] =
    for
      _ <- source.subscribe(receive)
      _ <- source match
        case hold: Hold[?] => receive(hold.asInstanceOf[Hold[T]].get)
        case _ => IO(())
    yield
      ()

  def ~ (signal: T): IO[Unit] =
    receive(signal)

  def <~ (signal: IO[T]): IO[Unit] =
    receive(signal)

  def <~ [F[_]: Traverse, In] (signals: F[IO[In]])(using ev: F[In] <:< T): IO[Unit] =
    this <~ Traverse[F].sequence(signals).map(x => ev(x))

  def <~ [F[_]: Traverse, In] (signals: SigSource[F[IO[In]]])(using ev: F[In] <:< T): IO[Unit] =
    signals.subscribe(seq => this <~ seq)

  