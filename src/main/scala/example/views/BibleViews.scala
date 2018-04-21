package example.views

import example.model.{BibleFile, BibleTestament, Book}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

object BibleViews {

  def bibleFile(b: BibleFile): Div = {

    div(cls :="bookPosition",
      div(cls := "shortIcon", p(b.shortBook, cls := "shortBook")),
      p(b.book, cls := "book"),
      button(cls := "btnPlay", i(cls := "fa fa-play")),
      p(b.name, cls := "duration"),
      p("", clear := "both", margin := "0 0")
    ).render
  }

  def bibleT(b:BibleTestament) = {

  }

  def book(b:Book): JsDom.TypedTag[Div] = div(
    b.name, b.getVersion("cała").map(bibleFile)
  )
}
