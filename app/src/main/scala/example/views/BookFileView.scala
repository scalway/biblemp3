package example.views

import example.model.BibleFile
import example.utils.Database
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._


class BibleFileView(val b:BibleFile, player: example.player.AudioPlayer) {
  val icon = i(cls := "fa fa-play").render

  def setPlaying(a:Option[Boolean]) = {
    val playing = a.getOrElse(false)
    view.classList.toggle("selected", a.isDefined)
    icon.classList.toggle("fa-play", !playing)
    icon.classList.toggle("fa-pause", playing)
  }

  val view: Div = {
    div(cls :="chapter",
      button(cls := "btnPlay", icon),
      p(b.versionPartName, cls := "book"),
      div(cls := "durationBox",
        i(cls := "fa fa-clock-o", aria.hidden := "true"),
        p(b.time, cls := "duration")
      ),
      onclick := { () => player.actions.toggle(b, true, Database.position(b)) },
      p("", clear := "both", margin := "0 0")
    ).render
  }
}
