package example

import example.model.BibleFile
import org.scalajs.dom

import scala.util.Try
import scalatags.JsDom.all._

object AudioPlayer {
  var last:Option[BibleFile] = None
  val underlaying = audio().render

  def toggle(s:BibleFile) = last match {
    case Some(l) if l == s =>
      last = None
      Database.position.apply(s)
      underlaying.pause()
    case other =>
      last.map(Database.position.set(_, underlaying.currentTime))
      dom.console.warn(s"playing $s")
      last = Some(s)
      underlaying.src = s.url
      underlaying.currentTime = Database.position.apply(s)
      underlaying.play()
  }

  def pause() = underlaying.pause()

  val playerView = div(

  )

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
