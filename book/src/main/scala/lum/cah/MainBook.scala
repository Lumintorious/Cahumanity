package lum.cah

import cats.effect.IOApp
import cats.syntax.traverse.*
import squigly.*
import lum.cah.data.Card
import lum.sig.Hold
import lum.cah.gui.CardBoard
import cats.effect.IO

object MainBook extends IOApp.Simple:
  val cards = Hold <~ Seq.fill(10)(Card("Something!", "Expansion"))

  val comps = Seq(
    cardPage,
    cardBoardPage,
    "Label" -> IO.pure(new Label("Hello?"))
  )

  def run =
    for
      comps1 <- comps.map((k, v) => v.map(elem => (k, elem))).sequence
      book = Book(comps1*)
      _ <- Component.mount(book)
    yield
      ()