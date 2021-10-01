package squigly

import lum.sig.Hold
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.document
import cats.effect.IO
import cats.syntax.traverse.*
import org.scalajs.dom.raw.Attr
import lum.sig.Frequency
import scala.collection.mutable.HashMap

def className(using c: Component) = c.className

trait Component(
  val className: Hold[String] = Hold("")
):
  def init: IO[Unit] = (className ~> { c => IO { if !c.isEmpty then html.classList.add(c) } })

  def html: HTMLElement

  object style:
    private val cache: HashMap[String, Frequency[String]] = HashMap.empty

    private def newFrequency(key: String): Frequency[String] =
      Frequency[String](value => IO { html.style.setProperty(key, value) } )
    
    def apply(key: CssKey): Frequency[String] =
      cache.getOrElseUpdate(key, newFrequency(key))

inline def it [T <: Component] (using comp: T) = comp

object Component:
  def mount(component: IO[Component]): IO[Unit] =
    component.map(c => document.body.appendChild(c.html))

  class Tagged(tag: String) extends Component():
    val html = document.createElement(tag).asInstanceOf[HTMLElement]

  def tagged(tag: String ) = Builder[Tagged](new Tagged(tag))

  class Builder[T <: Component](create: => T):
    def apply(funcs: (T => IO[Unit])*): IO[Component] =
      for
        elem <- IO(create)
        _ <- elem.init
        _ <- funcs.map(_.apply(elem)).sequence
      yield
        elem

  trait RequireCss:
    locally {
      val style = document.createElement("link")
      val className = getClass().nn.getName().nn
      val cssPath = "/static/" + className.replace("$", "").replace(".", "/") + ".css"
      style.setAttribute("href", cssPath)
      style.setAttribute("rel", "stylesheet")
      document.head.appendChild(style)
    }