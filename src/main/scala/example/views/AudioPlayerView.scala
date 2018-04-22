package example.views
import example.{AudioPlayer, Database}
import example.model.BibleFile
import org.scalajs.dom
import org.scalajs.dom.html.Audio
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

import scala.scalajs.js
import scalatags.JsDom.all.{span, _}
import org.scalajs.jquery.{JQuery, jQuery}

import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.ScalaJSDefined

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


@ScalaJSDefined
trait AmplitudeCallbacks extends js.Object {
  /** Occurs before the play method is called */
  var before_play:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs after the play method is called */
  var after_play:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs before the stop method is called */
  var before_stop:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs after the stop method is called */
  var after_stop:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs when the time has updated */
  var time_update:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs when an album changes */
  var album_change:UndefOr[js.Function0[Any]] = js.undefined

  /** Occurs when a song has been changed */
  var song_change:UndefOr[js.Function0[Any]] = js.undefined

  var after_pause:UndefOr[js.Function0[Any]] = js.undefined

}

object AudioPlayerView {
  val obj = js.Dynamic.literal
  def play(): Unit = {
    println("AudioPlayerView.play()")
    jQuery(".amplitude-play").click()
  }
  def pause() = {
    println("AudioPlayerView.pause()")
    jQuery(".amplitude-pause").click()
  }

  def sjsFunction[T](f:js.Function0[T]) = f

  def getCurrentSongFile() = {
    val url = songs(Amplitude.getActiveIndex()).url.asInstanceOf[String]
    songsOrginal.find(url == _.url)
  }


  def refresh() = dom.window.setTimeout(() => {
    getCurrentSongFile().map(AudioPlayer.onSongChange(_, !Amplitude.audio().paused))
  }, 200)

  val callbacks = new AmplitudeCallbacks {
    song_change = sjsFunction { () => refresh() }
    after_pause = sjsFunction{ () => refresh() }
    after_play = sjsFunction{ () => refresh() }
    after_stop = sjsFunction( () => refresh() )

    time_update = sjsFunction(() =>
      getCurrentSongFile().map { s =>
        dom.console.warn("time_update")
        Database.position.set(s, Amplitude.audio().currentTime)
      }
    )
  }

  var songsOrginal = Seq.empty[BibleFile]
  var songs: js.Array[js.Dynamic] = js.Array()
  var songVersions: js.Dictionary[js.Array[Int]] = js.Dictionary()

  def setPlaylist(list:Seq[BibleFile]) = {

    import scala.scalajs.js.JSConverters._

    songVersions =
      list.zipWithIndex
        .groupBy(_._1.version)
        .map(s => s._1 -> s._2.map(_._2).toJSArray)
        .toJSDictionary

    songsOrginal = list

    songs = list.map { book =>
      obj(
        name = book.book,
        artist = book.versionPartName,
        url = book.url
      ).asInstanceOf[js.Dynamic]
    }(collection.breakOut)

    Amplitude.init(obj(
      callbacks = callbacks,
      songs = songs,
      playlists = songVersions
    ))
  }

  def play(book:BibleFile, position:Double = 0): Unit = {
    val idx = songs.indexWhere(_.url == book.url)
    setSongItem.setAttribute("amplitude-song-index", idx.toString)
    setSongItem.setAttribute("amplitude-playlist", book.version)
    setSongItem.click()
    Amplitude.audio().currentTime = position
  }

  val progress = typedTag[HTMLElement]("progress")
  val amplitude = new DataAttribute(List("amplitude"))
  val setSongItem = span(cls := "amplitude-play", display.none).render
  val progressView = progress(cls := "amplitude-song-played-progress", amplitude.main.song.played.progress := "true", id := "song-played-progress").render

  def icon(name:String, icon:String) = div( cls:=("amplitude-button amplitude-" + name), i(cls:=("fa fa-" + icon)))
  val logoHover = div(cls:="hover", img(src:="assets/images/logo_01.png")).render

  val view = div( id:="single-song-player",
    div( cls:="bottom-container",
      div( cls:="control-container",
        icon("prev", "backward"),
        icon("stop", "stop"),
        div( cls:="amplitude-play-pause", amplitude.main.play.pause:="true", id:="play-pause"),
        icon("next", "forward"),

        //todo this items are udes only from code
        setSongItem,
        div( cls:="amplitude-pause", amplitude.main.play.pause:="true", display.none),
        div( cls:="amplitude-play", amplitude.main.play.pause:="true", display.none),

        div( cls:="meta-container",
          span( amplitude.song.info:="name", amplitude.main.song.info:="true", cls:="song-name"),
          span( amplitude.song.info:="artist", amplitude.main.song.info:="true")
        )
      ),

      div( cls:="time-container",
        span( cls:="current-time",
          span( cls:="amplitude-current-minutes", amplitude.main.current.minutes:="true"),":",span( cls:="amplitude-current-seconds", amplitude.main.current.seconds:="true")
        ),
        span( cls:="duration",
          span( cls:="amplitude-duration-minutes", amplitude.main.duration.minutes:="true"),":",span( cls:="amplitude-duration-seconds", amplitude.main.duration.seconds:="true")
        )
      ),
      progressView,
      logoHover
    )
  ).render

  progressView.addEventListener("click", (e:MouseEvent) => {
    val offset = progressView.getBoundingClientRect()
    val x = e.pageX - offset.left
    Amplitude.setSongPlayedPercentage(x / progressView.offsetWidth * 100 )
  })
}
