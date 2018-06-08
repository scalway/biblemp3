package example.views

import example.model.Bible
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

class BibleViews {
  val stid = Bible.ot.name.hashCode
  val ntid = Bible.nt.name.hashCode

  val view: Div = {
    div(
      cls := "tabContainer",
      ul(cls := "nav nav-tabs",
        li(cls := "testTab", a(data.toggle := "tab", href := "#"+stid, "Stary Testament")),
        li(cls := "testTab active", a(data.toggle := "tab", href := "#"+ntid, "Nowy Testament")),
        li(cls := "infoTab", a(data.toggle := "tab",
          padding := "1.18em 0",
          href := "#app-info",
          span(cls := "fa fa-info-circle fa-2x")
        )
        )
      )
    ).render
  }
}
