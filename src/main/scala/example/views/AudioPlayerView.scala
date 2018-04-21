package example.views
import example.model.BibleFile
import org.scalajs.dom.html.Audio
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

import scala.scalajs.js
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery
@js.native
object Amplitude extends js.Any {
  def init(a:js.Any) = js.native
  def playNow(a:js.Any) = js.native
  def audio():Audio = js.native
  def play():Unit = js.native
  def pause():Unit = js.native
  def getActiveIndex():Int = js.native
  def setSongPlayedPercentage(p:Double):Unit = js.native

}

object AudioPlayerView {
  val obj = js.Dynamic.literal

  def play() = jQuery(".amplitude-play-pause").click()
  def pause() = Amplitude.pause()

  def prepare(book:BibleFile, position:Double = 0, play:Boolean = false) = {
    Amplitude.audio().src = ""
    Amplitude.audio().pause()
    Amplitude.init(obj(
      songs = js.Array(
        obj(
          name = book.book,
          artist = book.versionPartName,
          url = book.url
        )
      )
    ))
      Amplitude.audio().currentTime = position

  }


  def initExample() = {
    Amplitude.init(obj(
      songs = js.Array( obj( name = "example", artist = "artist", url = "https://521dimensions.com/songs/ICameRunning-AncientAstronauts.mp3"))
    ))
  }

  val progress = typedTag[HTMLElement]("progress")
  val amplitude = new DataAttribute(List("amplitude"))

  val progressView = progress(cls := "amplitude-song-played-progress", amplitude.main.song.played.progress := "true", id := "song-played-progress").render

  val view = div( id:="single-song-player",
    div( cls:="bottom-container",
      progressView,
      div( cls:="time-container",
        span( cls:="current-time",
          span( cls:="amplitude-current-minutes", amplitude.main.current.minutes:="true"),":",span( cls:="amplitude-current-seconds", amplitude.main.current.seconds:="true")
        ),
        span( cls:="duration",
          span( cls:="amplitude-duration-minutes", amplitude.main.duration.minutes:="true"),":",span( cls:="amplitude-duration-seconds", amplitude.main.duration.seconds:="true")
        )
      ),

      div( cls:="control-container",
        div( cls:="amplitude-play-pause", amplitude.main.play.pause:="true", id:="play-pause"),
        div( cls:="meta-container",
          span( amplitude.song.info:="name", amplitude.main.song.info:="true", cls:="song-name"),
          span( amplitude.song.info:="artist", amplitude.main.song.info:="true")
        )
      )
    )
  )

  progressView.addEventListener("click", (e:MouseEvent) => {
    if( Amplitude.getActiveIndex() == 0 ){
      val offset = progressView.getBoundingClientRect()
      val x = e.pageX - offset.left
      Amplitude.setSongPlayedPercentage(x / progressView.offsetWidth * 100 )
    }
  })


//  document.getElementById('song-played-progress').addEventListener('click', function( e ){
//    var offset = this.getBoundingClientRect();
//    var x = e.pageX - offset.left;
//    Amplitude.setSongPlayedPercentage( ( parseFloat( x ) / parseFloat( this.offsetWidth) ) * 100 );
//  });


//
//  val view = div(
//    id:="content-wrap",
//    div( id:="content",
//      div( id:="controls",
//        span( id:="previous-btn",i( cls:="fa fa-step-backward fa-fw", aria.hidden:="true")),
//        span( id:="play-btn",i( cls:="fa fa-play fa-fw fa-pause", aria.hidden:="true")),
//        span( id:="next-btn",i( cls:="fa fa-step-forward fa-fw", aria.hidden:="true"))
//      ),
//      div( id:="timeline",
//        span( id:="current-time","1:25"),
//        span( id:="total-time","2:55"),
//        div( cls:="slider", data.direction:="horizontal",
//          div( cls:="progress", style:="width: 48.7035%;",
//            div( cls:="pin", id:="progress-pin", data.method:="rewind")
//          )
//        )
//      ),
//      div( id:="sub-controls",
//        i( cls:="fa fa-random", aria.hidden:="true"),
//        i( cls:="fa fa-refresh", aria.hidden:="true"),
//        i( cls:="fa fa-bluetooth-b active", id:="bluetooth-btn", aria.hidden:="true"),
//        i( cls:="fa fa-heart-o", id:="heart-icon", aria.hidden:="true")
//      )
//    )
//  )
}
