package example.views.player

import example.model.Book

import scalatags.JsDom.all._

class BibleHeaderView(b:Book, color:String) {
  private val icon = div(
    cls := "shortIcon",
    backgroundColor := color,
    p(
      b.short,
      cls := "shortBook"
    )
  ).render

  val view = div(
    cls := "bookHeader",
    icon,
    div(
      cls := "bookName",
      p(
        b.name,
        cls := "bookNameText"
      )
    ),
    div(clear := "both")
  ).render

  def markAsStarted(force:Boolean = true) = icon.style.borderLeft = if (force) "6px solid gray" else ""
}