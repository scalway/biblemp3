package example

import example.model.BibleFile
import org.scalajs.dom

import scalatags.JsDom.all._

object AudioPlayer {
  var last:Option[BibleFile] = None
  val underlaying = audio().render

  def toggle(s:BibleFile) = last match {
    case Some(l) if l == s =>
      last = None
      underlaying.pause()
    case other =>
      dom.console.warn(s"playing $s")
      last = Some(s)
      underlaying.src = s.url
      underlaying.play()
  }

  def pause() = underlaying.pause()
}
