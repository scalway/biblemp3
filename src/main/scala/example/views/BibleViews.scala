package example.views

import example.AudioPlayer
import example.model.{BibleFile, BibleTestament, Book}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery

object BibleViews {
  def bibleFile(b: BibleFile): Div = {
    div(cls :="chapter",
      button(cls := "btnPlay", i(cls := "fa fa-play")),
      p(b.versionPartName, cls := "book"),
      p(b.name, cls := "duration"),
      onclick := { () => AudioPlayer.toggle(b) },
      p("", clear := "both", margin := "0 0")
    ).render
  }

  def bibleTestament(b:BibleTestament) = {
    val bid = b.name.hashCode
    val header = div(
      data.toggle := "collapse",
      data.target := "#" + bid,
      cls := "testamentHeader",
      p(b.name, cls := "testamentHeaderText")
    ).render
    val content = div(
      id := bid,
      cls := "collapse",
      b.books.map(book)
    ).render
  }

  def book(b:Book): JsDom.TypedTag[Div] = {
    val cid = "chapter-" + b.short
    val chapters = div(
      id:=cid,
      cls:="collapse",
      b.files.map(bibleFile)
    ).render
    val header = div(
      data.toggle := "collapse",
      data.target := "#" + cid,
      cls := "bookHeader",
      div(cls := "shortIcon",
          p(b.short, cls := "shortBook")
      ),
      div(cls := "bookName", p(b.name, cls := "bookNameText")),
      div(clear := "both")
    ).render

    div(header, chapters, p("", clear := "both", margin := "0 0"))
  }
}
