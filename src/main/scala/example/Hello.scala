package example

import example.model.BibleFile
import example.views.BibleViews
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Heading}
import scalatags.JsDom.all._

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
    BibleMp3Data.NT.map(BibleViews.bibleFile),
    footer
  ).render
}
