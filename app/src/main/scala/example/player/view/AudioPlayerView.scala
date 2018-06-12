package example.player.view

import example.player.{AudioPlayer, PlayerShortcuts}
import example.utils.{Bootstrap, DurationUtils}
import example.utils.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

class AudioPlayerView(val player:AudioPlayer) extends PlayerShortcuts(player) {
  val seek = new ProgressSeek(player)

  def icon(icon:String, f:() => Any) = div( cls := "amplitude-button", i(cls:=("fa fa-" + icon)), onclick := f)
  def fa(icon:String) = div(cls:="amplitude-button", i(cls:=("fa fa-" + icon)))
  def scrollBtn(time:Double) = div(cls:="amplitude-button app-amplitude-button-text", onclick := { () =>
    player.actions.seek(time)
  })

  val logoHover: Div = div(cls:="hover", img(src:="assets/images/logo_01.png")).render

  def hideCover() = {
    dom.window.setTimeout(() => logoHover.style.display = "none", 1000)
    logoHover.style.opacity = "0.0"
  }

  private val controlPanel = div(
    textAlign.center, margin.auto,
    id:= "app-control-panel-box",
    div(
      cls := "app-control-panel",
      icon("backward", () => $.prev()),
      icon("stop", () => $.stop()),
      scrollBtn(-30)("-30s"),
      scrollBtn(-10)("-10s"),
      scrollBtn(10)("+10s"),
      scrollBtn(30)("+30s"),
      icon("forward", () => $.next()),
      div( cls:="amplitude-play", onclick := {() => $.toggle() }, display.none),
    )
  ).render

  val menuView = fa("thumb-tack").render

  val metaContainerView = div(cls := "meta-container",
    span(cls:="player-song-title", *.song.map(_.book)),
    span(cls:="player-song-artist", *.song.map(_.versionPartName)),
  ).render

  val view: Div = div( id:="single-song-player",
    div( cls:="bottom-container",
      div( cls:="control-container",
        menuView,
        div( onclick := {() => $.toggle() },
          id:="play-pause",
          cls:="amplitude-play-pause",
          new Modifier {
            val clss = Seq("amplitude-paused", "amplitude-playing")
            override def applyTo(t: Element): Unit = *.isPlaying.subscribe { play =>
              val clss2 = if(play) clss else clss.reverse
              t.classList.remove(clss2.head)
              t.classList.add(clss2.last)
            }
          }
        ),
        metaContainerView
      ),


      div( cls:="time-container",
        span( cls:="current-time", *.position.map(DurationUtils.preetyDuration)),
        span( cls:="duration", *.duration.map(DurationUtils.preetyDuration))
      ),
      seek.view,
      controlPanel,
      logoHover
    )
  ).render

  Bootstrap.collapse(menuView, controlPanel)
  *.song.first.subscribe(b => hideCover())
}

class AudioPlayerView2(val player:AudioPlayer) extends PlayerShortcuts(player) {
  val seek = new ProgressSeek(player)

  private val playBtn: Button = button("play", onclick := { () => $.play() }).render
  private val pauseBtn: Button = button("pause", onclick := { () => $.pause() }).render
  private val toggleBtn: Button = button(
    "toggle",
    *.isPlaying.map(if (_) color.red else color.black),
    onclick := { () => $.toggle() }
  ).render

  val view = div(
    player.htmlAudio,
    seek.view,
    playBtn,
    pauseBtn,
    toggleBtn,
    span(*.song.map(s => s.book + " " +  s.versionPartName)),
    span(*.position.map(_.toString))
  ).render
}



