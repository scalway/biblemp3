package rabbi.views.player

import rabbi.model.{BibleFile, BibleTestament}
import rabbi.player.AudioPlayer
import rabbi.utils.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html.Div
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import scalatags.JsDom.all._

class BibleTestamentView(b:BibleTestament, colors:Seq[String], player:AudioPlayer) {
  val colorMapping =
    b.books.groupByOrdered(_.group).map(_._1).zip(colors).toMap
      .withDefaultValue("gray")

  val booksViews = b.books.map(s => new BookView(s, colorMapping(s.group), player))

  def show(ref:BibleFile) = {
    println("show:" + ref)

    val idx = b.books.indexWhere(b => b.files.contains(ref))
    if (idx >= 0) {
      //select proper tab
      jQuery(s".nav-tabs a[href='#${b.name.hashCode}']").asInstanceOf[js.Dynamic].tab("show")
      //scroll to proper item
      val bookV = booksViews(idx)
      jQuery(bookV.chapters).asInstanceOf[js.Dynamic].collapse("show")
      bookV.header.view.smothScrollToTopJQ(300, dom.document.body.parentElement)

    }
  }

  val view: Div = {
    div(
      id := b.name.hashCode,
      cls := "tab-pane",
      booksViews.map(_.view)
    ).render
  }
}
