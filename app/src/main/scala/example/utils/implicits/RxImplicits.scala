package example.utils.implicits

import org.scalajs.dom
import org.scalajs.dom.Element
import rxscalajs.Observable
import rxscalajs.subjects.BehaviorSubject

import scala.scalajs.js
import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.generic.StylePair

trait RxImplicits {
  implicit def observableOfModifier2Modifier(observable: Observable[Modifier]): Modifier {
    def applyTo(t: Element): Unit
  } = new Modifier {
    override def applyTo(t: Element): Unit = {
      observable.subscribe (_.applyTo(t))
    }
  }

  implicit def observableOfString2Modifier(observable: Observable[String]): Modifier {
    def applyTo(t: Element): Unit
  } = new Modifier {
    override def applyTo(t: Element): Unit = {
      val textnode = span().render
      observable.subscribe (textnode.textContent = _)
      t.appendChild(textnode)
    }
  }

  implicit def obsstyle2modifier[T](obs:Observable[StylePair[dom.Element, T]]):Modifier = new Modifier {
    override def applyTo(t: Element): Unit = obs.subscribe(_ applyTo t)
  }

  implicit def obsattr2modifier[T](obs:Observable[AttrPair]):Modifier = new Modifier {
    override def applyTo(t: Element): Unit = obs.subscribe(_ applyTo t)
  }



  implicit class RxBehaviorSubject[T](val bs: BehaviorSubject[T]) {
    def getValue() = bs.inner.asInstanceOf[js.Dynamic].getValue().asInstanceOf[T]
    def edit(f:T => T): Observable[T] = {
      val res = bs.take(1)
      bs.next(f(getValue()))
      res
    }
  }
}
