package experiments.mrx

import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/** Component Implicit Argument */
//class Cia(val rxCtx:ComponentCtx) extends AnyVal
//
//object Cia {
//  implicit def createCia(implicit ctx:ComponentCtx): Cia = new Cia(ctx)
//}

case class ComponentCtx(
  val rxCtx:MonadicRxCtx = new MonadicRxCtx()
) {
  def fork() = copy(rxCtx = rxCtx.subctx())
}

abstract class Component(implicit ctx0:ComponentCtx) {
  implicit val ctx = ctx0.fork()
  implicit val rxCtx: MonadicRxCtx = ctx.rxCtx

  def render:HTMLElement
}


object Component {
  implicit def component2Modifier(c:Component):Modifier = c.render
  implicit def component2Html(c:Component):HTMLElement = c.render
}