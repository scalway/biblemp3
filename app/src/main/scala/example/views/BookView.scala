package example.views

import example.model.Book
import example.player.AudioPlayer
import example.utils.Bootstrap

import scalatags.JsDom.all._

case class BookView(b:Book, color:String, player:AudioPlayer) {
  val fileViews = b.files.map(s => new BibleFileView(s, player))

  val header = div(
    cls := "bookHeader",
    div(cls := "shortIcon", backgroundColor := color,
      p(b.short, cls := "shortBook")
    ),
    div(cls := "bookName", p(b.name, cls := "bookNameText")),
    div(clear := "both")
  ).render

  val chapters = div(
    id:="chapter-" + b.short.hashCode, fileViews.map(_.view)
  ).render

  val view = div(Bootstrap.collapse(header, chapters)).render
}

