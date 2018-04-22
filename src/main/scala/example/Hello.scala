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
    ntView.view.classList.add("active")
    AudioPlayerView.setPlaylist(Bible.all.files)
  }

  val colorsST = Seq("#e00b3c", "#9a13dd", "#1357dd", "#13ddae", "#13b5dd")
  val colorsNT = Seq("#ddac25", "#6113dd", "#13b5dd", "#d7dd13")

  val header: Div = div().render

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



