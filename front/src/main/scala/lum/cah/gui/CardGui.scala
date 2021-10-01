package lum.cah
package gui

import data.Card
import lum.sig.Frequency
import concurrent.duration.DurationInt
import squigly.*

object CardGui extends Component.RequireCss:
  inline def Text(text: String) = Label(
    _.text ~ text,
    _.className ~ "text"
  )

  inline def Expansion(expansion: String) = Label(
    _.text ~ expansion,
    _.className ~ "expansion"
  )

  inline def Wrapper(card: Card) = Div(
    _.className ~ "card",
    _.children ~ Seq(
      Text(card.text),
      Expansion(card.expansion),
    )
  )

  def apply(card: Card) =
    Wrapper(card)