package example.player.view

import example.player.{AudioPlayer, PlayerShortcuts}
import org.scalajs.dom.{Element, MouseEvent}
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scalatags.JsDom.all._

class ProgressSeek(val player:AudioPlayer) extends PlayerShortcuts {

  private val progress = typedTag[HTMLElement]("progress")

  val view = progress(cls := "amplitude-song-played-progress", new Modifier {
    override def applyTo(t: Element): Unit = *.progress.subscribe { s =>
      if (!s.isInfinite) t.asInstanceOf[js.Dynamic].value =  s
    }
  }).render

  view.onclick = { (e:MouseEvent) =>
    val offset = view.getBoundingClientRect()
    val x = e.pageX - offset.left

    val progress = x / view.offsetWidth
    player.actions.setPositionPercentage(progress)
  }
}