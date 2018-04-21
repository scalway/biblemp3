package example

import example.model.BibleFile
import example.views.{Amplitude, AudioPlayerView}
import org.scalajs.dom

import scala.util.Try
import scalatags.JsDom.all._

object AudioPlayer {
  var last:Option[BibleFile] = None

  def toggle(s:BibleFile):Unit = last match {
    case Some(l) if l == s =>
      last = None
      Database.position.apply(s)
      AudioPlayerView.pause()
      AudioPlayerView.initExample()
      ()
    case other =>
      last.map(Database.position.set(_, Amplitude.audio().currentTime))
      dom.console.warn(s"playing $s")
      last = Some(s)
      AudioPlayerView.prepare(s, Database.position.apply(s))
      AudioPlayerView.play()
  }

  def pause() = AudioPlayerView.pause()
}

class DBAccessor[T, V](name:String, key:T => String, v2s:V => String, s2v:String => V, default:V) {
  def set(a:T, v:V) = {
    dom.window.localStorage.setItem(name+ "-" + key(a), v2s(v))
  }

  def get(a:T):Option[V] = Try(
    s2v(dom.window.localStorage.getItem(name+ "-" + key(a)))
  ).toOption

  def apply(a:T):V = Try(
    s2v(dom.window.localStorage.getItem(name+ "-" + key(a)))
  ).getOrElse(default)
}

object Database {
  object position extends DBAccessor[BibleFile, Double](
    "position", _.url, _.toString, _.toDouble, 0
  )
}
