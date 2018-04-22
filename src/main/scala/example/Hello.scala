package example

import example.model.{Bible, BibleFile}
import example.views.{AudioPlayerView, BibleTestamentView, BibleViews, InfoView}
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Heading}
import org.scalajs.jquery.jQuery
import scalatags.JsDom.all._

object Hello {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(view)

    AudioPlayer.songListeners :+= { (old:Option[BibleFile], newB:BibleFile, isPlaying:Boolean) =>
      println("song changed from = " + old)
      println(s"song changed to ($isPlaying) = " + newB)
      val oldB = old.getOrElse(BibleFile.empty)
      val all = ntView.booksViews.flatMap(_.fileViews)
      all.collect { case x if x.b.url == oldB.url => x.setPlaying(None) }
      all.collect { case x if x.b.url == newB.url => x.setPlaying(Some(isPlaying)) }
    }

    ntView.view.classList.add("active")
    otView.view.classList.add("active")
    AudioPlayerView.setPlaylist(Bible.all.files)


  }

  val colorsST = Seq("#e00b3c", "#9a13dd", "#1357dd", "#13ddae", "#13b5dd")
  val colorsNT = Seq("#ddac25", "#6113dd", "#13b5dd", "#d7dd13")

  val header: Div = div().render
  val footer: Div = div(
    id := "footer",
    p("Biblia-mp3 2018")
  ).render

  val ntView = new BibleTestamentView(Bible.nt, colorsNT)
  val otView = new BibleTestamentView(Bible.ot, colorsST)

  val view: Div = div(
    header,
    div(id := "stickyMenu",
      AudioPlayerView.view,
      new BibleViews().view
      ),
      div(cls := "tab-content",
        ntView.view,
        otView.view,
        div(id := "app-info", cls := "tab-pane", new InfoView().view)
      ),
      footer
  ).render
}



