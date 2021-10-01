package squigly

import lum.sig.Hold
import cats.effect.IO
import org.scalajs.dom.raw.HTMLElement

def setChildren(html: HTMLElement, seq: Seq[Component]) = IO[Unit] {
    html.innerHTML = ""
    seq.map(_.html).foreach(html.appendChild)
  }

def children(using c: HasChildren) = c.children

trait HasChildren(val children: Hold[Seq[Component]] = Hold(Seq.empty)) extends Component:
  override def init = super.init >> (children ~> setChildren)

  protected def setChildren(seq: Seq[Component]) = IO[Unit] {
    html.innerHTML = ""
    seq.map(_.html).foreach(html.appendChild)
  }