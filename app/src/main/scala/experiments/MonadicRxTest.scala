package experiments

import experiments.mrx.{Component, ComponentCtx, MonadicRxCtx}
import mhtml._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import experiments.mrx.Implicits._
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement

import scala.util.Random
import scalatags.JsDom
@JSExport
class MonadicRxTest {
  val a = Var(11)
  val b = Var(11)

  val c = for (aa <- a; bb <- b) yield {
    aa + bb
  }

  val d = (a.rx, b.rx, c).map3 { (a,b,c) =>
    a + b
  }


  class ExampleComponent(implicit ctx:ComponentCtx) extends Component {
    val render:Div = div(
      div( cls:= "well",
        c.map(_.toString),
        onclick := { () =>
          a.update(_ + 1)
        }
      ),

      span( cls:= "well",
        c.map("Koza" + _),
        onclick := { () =>
          rxCtx.cancel()
        }
      )
    ).render
  }

  class SimpleTextComponent(implicit ctx0:ComponentCtx) extends Component {
    val render: HTMLElement = span(color.red, backgroundColor := "pink", padding := 10.px, c.map(_.toString)).render
  }

  class ExampleComponent2(implicit ctx0:ComponentCtx) extends Component {
    val textComp = new SimpleTextComponent()(ctx) {
      render.applyTags(
        onclick := { () => rxCtx.cancel() }
      )
    }

    val render: Div = {
      (a.rx, b.rx, d).map3 { (a1, b1, d1) =>

        div(
          div( cls:= "well", a1,
            div("55"),
            onclick := { () =>
              a.update(_ + 1)
              b.update(_ + Random.nextInt(4))
            }
          ),
          textComp,
          span( cls:= "well",
            s"Koza $b1 $a1 $d1 $c $d",
            onclick := { () =>
              rxCtx.cancel()
            }
          )
        ).render
      }
    }.asDiv.render
  }

  @JSExport
  def main(): Unit = {
    implicit val ctx = new ComponentCtx()

    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(new ExampleComponent2().render)
    dom.document.body.appendChild(new ExampleComponent().render)
  }
}
