package example.utils

import org.scalajs.dom.raw.HTMLElement

object Bootstrap {
  def collapse(header:HTMLElement, toCollapse:HTMLElement) = {
    lazy val nidgen:String = toCollapse.hashCode().toString
    val nid = Option(toCollapse.id).getOrElse(nidgen)
    toCollapse.id = nid
    toCollapse.classList.add("collapse")

    header.setAttribute("data-toggle", "collapse")
    header.setAttribute("data-target", "#" + nid)
    header.classList.add("collapsed")
    Seq(header, toCollapse)
  }
}