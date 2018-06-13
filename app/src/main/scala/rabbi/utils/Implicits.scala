package rabbi.utils

import rabbi.utils.implicits.RxImplicits
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery.jQuery

import collection.mutable.{LinkedHashMap, LinkedHashSet, Map => MutableMap}
import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic



object Implicits extends RxImplicits {
  val obj = scalajs.js.Dynamic.literal

  class GroupByOrderedImplicitImpl[A](val t: Traversable[A]) extends AnyVal {
    def groupByOrdered[K](f: A => K): MutableMap[K, LinkedHashSet[A]] = {
      val map = LinkedHashMap[K,LinkedHashSet[A]]().withDefault(_ => LinkedHashSet[A]())
      for (i <- t) {
        val key = f(i)
        map(key) = map(key) + i
      }
      map
    }
  }

  implicit def traversableToTrOps[T](t: Traversable[T]): GroupByOrderedImplicitImpl[T] =
    new GroupByOrderedImplicitImpl(t)


  implicit class JSUtilHtmlElement[T <: HTMLElement](a:T) {
    def scrollTo(): Unit = {
      if (a != null)
        if (dom.window.navigator.userAgent.indexOf("Firefox") != -1)
          a.asInstanceOf[js.Dynamic].scrollIntoView(Dynamic.literal(behavior = "smooth"))
        else {
          a.asInstanceOf[js.Dynamic].scrollIntoViewIfNeeded()
        }
    }

    def smothScrollToTopJQ(additionalOffset: Int = 0, offsetParent: HTMLElement = null): T = {
      val op = if (offsetParent != null) offsetParent else a.offsetParent
      if (a != null) {
        jQuery(op).stop().animate(obj(scrollTop = a.offsetTop - additionalOffset), 300)
      }
      a
    }
  }


  implicit class ExtensionToAnything[T](a:T) {
    @inline def === (b:T) = a == b
  }
}
