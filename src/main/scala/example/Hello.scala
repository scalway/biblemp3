package example

import example.model.BibleFile
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Heading}
import scalatags.JsDom.all._

object Hello {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(view)
    dom.window.setTimeout(() => {
      header.style.color = "pink"
    }, 2200)
  }

  val header: Heading = h1("hello world").render

  val view: Div = div(
    header,
    BibleMp3Data.NT.map(item),
    footer
  ).render

  def item(b: BibleFile): Div = {
    div(cls :="bookPosition",
      div(cls := "shortIcon", p(b.shortBook, cls := "shortBook")),
      p(b.book, cls := "book"),
      button(cls := "btnPlay", i(cls := "fa fa-play")),
      p(b.name, cls := "duration"),
      p("", clear := "both", margin := "0 0")
    ).render
  }
}
