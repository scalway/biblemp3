package example.utils.bootstrap

import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

class BTabs() {
  case class Tab(name:Modifier, view:HTMLElement) {
    val id = if (view.id.nonEmpty) view.id else name.hashCode.toString
    view.id = id
    val headerView = li(cls := "testTab", a(data.toggle := "tab", href := "#"+id, name)).render
  }

  var tabs = Seq.empty[Tab]

  def tab(name:Modifier, view:HTMLElement) = {
    val ctab = Tab(name, view)
    tabs :+= ctab
    ctab
  }

  lazy val headerView =  div(
    cls := "tabContainer",
    ul(cls := "nav nav-tabs",
      tabs.map(_.headerView)
    )
  ).render

  lazy val contentView = div(cls := "tab-content", tabs.map(_.view))
}