package squigly

import lum.sig.Hold
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.raw.HTMLCollection
import cats.effect.IO
import cats.syntax.traverse.*

object Div extends Component.Builder(new Div())

class Div(
  className: Hold[String] = Hold("")
) extends Component(className) with HasChildren():
  val html: HTMLElement = document.createElement("div").asInstanceOf