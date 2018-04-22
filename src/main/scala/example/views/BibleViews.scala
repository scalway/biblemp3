package example.views

import example.AudioPlayer
import example.model.{Bible, BibleFile, BibleTestament, Book}
import org.scalajs.dom.html.Div
import example.utils.Implicits._
import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery

object View {
  val stid = Bible.ot.name.hashCode
  val ntid = Bible.nt.name.hashCode
}

class BibleViews {
  val view: Div = {
    div(
      cls := "tabContainer",
      ul(cls := "nav nav-tabs",
        li(a(data.toggle := "tab", href := "#"+View.stid, "Stary Testament")),
        li(a(data.toggle := "tab", href := "#"+View.ntid, "Nowy Testament"))
      )
    ).render
  }
}

class BibleTestamentView(b:BibleTestament, colors:Seq[String]) {
  val colorMapping =
    b.books.groupByOrdered(_.group).map(_._1).zip(colors).toMap
    .withDefaultValue("gray")

  val booksViews = b.books.map(s => new BookView(s, colorMapping(s.group)))

  val view: Div = {
    /*val bid = b.name.hashCode

    val header = div(
      data.toggle := "collapse",
      data.target := "#" + bid,
      cls := "testamentHeader collapsed",
      div(cls := "testamentHeaderTextContainer",
        p(b.name, cls := "testamentHeaderText")
      )
    ).render

    val content = div(
      id := bid,
      cls := "collapse",
      booksViews.map(_.view)
    ).render

    div(cls := "testament",
      header,
      content).render*/

    div(
      id := b.name.hashCode,
      cls := "tab-pane",
      booksViews.map(_.view)
    ).render
  }
}

case class BookView(b:Book, color:String) {
  val fileViews = b.files.map(s => new BibleFileView(s))

  val view: JsDom.TypedTag[Div] = {
    val cid = "chapter-" + b.short
    val chapters = div(
      id:=cid,
      cls:="collapse",
      fileViews.map(_.view)
    ).render

    val header = div(
      data.toggle := "collapse",
      data.target := "#" + cid,
      cls := "bookHeader collapsed",
      div(cls := "shortIcon", backgroundColor := color,
        p(b.short, cls := "shortBook")
      ),
      div(cls := "bookName", p(b.name, cls := "bookNameText")),
      div(clear := "both")
    ).render

    div(
      header,
      chapters,
      p("", clear := "both", margin := "0 0")
    )
  }
}


class BibleFileView(b:BibleFile) {
  val icon = i(cls := "fa fa-play").render

  def setPlaying(a:Boolean) = {
    icon.classList.toggle("fa-play", a)
    icon.classList.toggle("fa-pause", !a)
  }

  val view: Div = {
    div(cls :="chapter",
      button(cls := "btnPlay", icon),
      p(b.versionPartName, cls := "book"),
      div(cls := "durationBox",
        i(cls := "fa fa-clock-o", aria.hidden := "true"),
        p(b.name, cls := "duration")
      ),
      onclick := { () => AudioPlayer.toggle(b) },
      p("", clear := "both", margin := "0 0")
    ).render
  }
}
