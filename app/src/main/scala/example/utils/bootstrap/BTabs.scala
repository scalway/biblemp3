package example.utils.bootstrap

import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/** abstraction over Bootstrap Tabs. It allows to create tabs that are always in sync (header and content)
  *
  * use it like this:
  * {{{
  *   new BTabs {
  *     tab("test1", div("content1").render)
  *     tab("test2", div("content2").render)
  *     ...
  *   }
  * }}}
  *
  * */
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