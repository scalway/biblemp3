package experiments.mrx

import cats.syntax.TupleSyntax
import mhtml.{Rx, Var}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all.{Modifier, div, span}

trait MonadicRxCatsSupport {
  import mhtml.implicits.cats
  @inline implicit def mhtmlRxMonadIntstance = cats.mhtmlRxMonadIntstance
  @inline implicit def mhtmlRxSemigroupIntstance[A] = cats.mhtmlRxSemigroupIntstance[A]
  @inline implicit def mhtmlVarSyntaxCartesian[A](fa:Var[A]) = cats.mhtmlVarSyntaxCartesian(fa)
  @inline implicit def mhtmlVarSyntaxSemigroup[A](fb:Var[A]) = cats.mhtmlVarSyntaxSemigroup(fb)
}

object Implicits extends TupleSyntax with MonadicRxCatsSupport {
  implicit class VarOps[T](val v:Rx[T]) {
    //just cast Var to Rx. We need it with e.g. cats tuple syntax
    //that'll work only for Rx not for Vars
    def rx:Rx[T] = v
  }

  implicit class RxHtmlOps[T <: HTMLElement](val v:Rx[T])(implicit rxCtx: MonadicRxCtx) {
    def asDiv = div(asModifier)
    def asSpan = span(asModifier)
    def asModifier = new RxHtml2Modifier(v)
  }

  implicit class RxString2Modifier(val rx:Rx[String])(implicit ctx: MonadicRxCtx) extends Modifier {
    override def applyTo(t: Element): Unit = {
      val text = span.render
      t.appendChild(text)
      ctx.run(rx)(a => text.innerHTML = a)
    }
  }

  implicit class RxHtml2Modifier(val rx:Rx[HTMLElement])(implicit rxCtx: MonadicRxCtx) extends Modifier {
    override def applyTo(t: Element): Unit = {
      val text = div.render
      t.appendChild(text)
      rxCtx.run(rx){ a =>
        text.innerHTML = ""
        text.appendChild(a)
      }
    }
  }
}
