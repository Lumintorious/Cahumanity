package lum.cah

import cats.effect.IO
import lum.sig.Hold
import squigly.*
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.document

object Summary extends Component.Builder(new Summary())

class Summary() extends Component() with HasChildren():
  val html: HTMLElement = document.createElement("summary").asInstanceOf

object Details extends Component.Builder(new Details())

class Details extends Component.Tagged("details") with HasChildren():
  override def init = super.init *> (summary ~> setSummary) *> (content ~> setContent)

  val content = Hold[Component](new Div())
  val summary = Hold[Component](new Div())

  protected def setSummary(comp: Component) =
    for
      summ <- Summary(_.children ~ Seq(comp))
      cont <- content.get
      _ <- setChildren(Seq(summ, cont))
    yield
      ()

  protected def setContent(comp: Component) =
    for
      cmp <- IO { Seq(comp) }
      summ <- summary.get.map(Seq(_))
      _ <- setChildren(summ ++ cmp)
    yield
      ()

def Page(comp: (String, Component)): IO[Component] =
  Details(
    _.content ~ comp(1),
    _.summary <~ Label(_.text ~ comp(0))
  )

def Book(templates: (String, Component)*): IO[Component] =
  Div(
    _.children <~ templates.map((k, v) => Page((k, v)))
  )