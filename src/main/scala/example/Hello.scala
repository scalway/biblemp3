package example

import org.scalajs.dom
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

case class BibleFile(
  url:String,
  book:String,
  shortBook:String,
  version:String,
  versionPartName:String,
  duration:String,
  bookKind:String
)

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
      p(b.duration, cls := "duration"),
      p("", clear := "both", margin := "0 0")
    ).render
  }
}
