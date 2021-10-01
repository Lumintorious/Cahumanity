package lum.cah.gui

import lum.sig.Hold
import lum.cah.data.Card
import cats.effect.IO
import lum.cah.MainClient
import squigly.Component.RequireCss
import squigly.Div

object CardBoard extends RequireCss:
  def apply(cards: Hold[Seq[Card]]) =
    Div(
      _.className  ~ "card-board",
      _.children  <~ cards.mapDeep(CardGui(_))
    )