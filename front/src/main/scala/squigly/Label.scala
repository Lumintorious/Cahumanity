package squigly

import lum.sig.*
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.document
import cats.effect.IO

object Label extends Component.Builder(new Label()):
  inline def apply = new Label(_, _)

class Label(
  val text: Hold[String] = Hold(""),
  className: Hold[String] = Hold(""),
) extends Component(className):
  override def init =
    super.init >> text ~> setText

  def setText(text: String) = IO[Unit] {
    html.innerText = text
  }

  val html: HTMLElement = 
    val html = document.createElement("label").asInstanceOf[HTMLElement]
    html