package rabbi.utils

import rabbi.model.BibleFile
import org.scalajs.dom
import rxscalajs.subjects.BehaviorSubject

import scala.util.Try
import upickle.default._
import rabbi.utils.Implicits._

object Database {
  class DBAccessorOne[T](name:String, v2s:T => String, s2v:String => T, default:T) {
    def set(a:T) = {
      dom.console.warn(s"$name.set($a)")
      dom.window.localStorage.setItem(name, v2s(a))
    }
    def get():Option[T] = Try(s2v(dom.window.localStorage.getItem(name))).toOption
    def apply():T = Try(s2v(dom.window.localStorage.getItem(name))).getOrElse(default)
  }

  class DBAccessor[T, V](val name:String, key:T => String, v2s:V => String, s2v:String => V, default:V) {
    val stream = BehaviorSubject[(T,V)](null)

    def set(a:T, v:V) = {
      stream.next(a -> v)
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
  ) {
    def getAll() = readAllKeys collect {
      case t
        if t._1 != null
          && t._2 != null
          && t._1.startsWith(name)
          && t._1.length > position.name.length + 2 => t
    }

    def init(base:Seq[BibleFile]) = {
      getAll().foreach {
        case (k, v) =>
          val rk = k.drop(name.length + 1)
          base.find(_.url === rk).foreach(res =>
            stream.next(res -> v.toDouble)
          )
      }
    }
  }

  def readAllKeys = ( 0 to dom.window.localStorage.length).map { i =>
    val key = dom.window.localStorage.key(i)
    val value = dom.window.localStorage.getItem(key)
    (key, value)
  }


}
