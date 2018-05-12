package example.utils

import example.model.BibleFile
import org.scalajs.dom

import scala.util.Try
import upickle.default._


object Database {
  class DBAccessorOne[T](name:String, v2s:T => String, s2v:String => T, default:T) {
    def set(a:T) = dom.window.localStorage.setItem(name, v2s(a))
    def get():Option[T] = Try(s2v(dom.window.localStorage.getItem(name))).toOption
    def apply():T = Try(s2v(dom.window.localStorage.getItem(name))).getOrElse(default)
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


  object lastItemUrl extends DBAccessorOne[String]("lastPosition", s => s, s=> s, "")

  object position extends DBAccessor[BibleFile, Double](
    "position",
    key = _.url,
    v2s = _.toString,
    s2v = _.toDouble,
    default = 0
  )
}
