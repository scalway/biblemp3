package example

import example.model.{Bible, BibleFile}
import example.views.{AudioPlayerView, BibleTestamentView, BibleViews}
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Heading}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

object Hello {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(view)
    dom.window.setTimeout(() => {
      header.style.color = "pink"
    }, 2200)
  }
  val colorsST = Seq("#e00b3c", "#9a13dd", "#1357dd", "#13ddae", "#13b5dd")
  val colorsNT = Seq("#ddac25", "#6113dd", "#13b5dd", "#d7dd13")

  val header: Heading = h1("hello world").render

  val view: Div = div(
    header,
    AudioPlayerView.view,
    new BibleTestamentView(Bible.nt, colorsNT).view,
    new BibleTestamentView(Bible.ot, colorsST).view,
    footer
  ).render
}



