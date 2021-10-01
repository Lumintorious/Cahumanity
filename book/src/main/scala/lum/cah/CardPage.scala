package lum.cah

import lum.cah.gui.CardGui
import lum.cah.data.Card
import lum.sig.Hold
import lum.cah.gui.CardBoard

val card = Card("Some text", "Expansion")
val cards = Hold <~ Seq.fill(10)(card)

val cardPage = "Card" -> CardGui(card)
val cardBoardPage = "Card Board" -> CardBoard(cards)
