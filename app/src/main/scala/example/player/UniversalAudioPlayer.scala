package example.player

import org.scalajs.dom
import org.scalajs.dom.Event
import rxscalajs.Observable
import rxscalajs.subjects.BehaviorSubject

import scalatags.JsDom.all.audio
import example.utils.Implicits._

case class State[T](playlist:Seq[T], songIndex:Int, empty:T) {
  lazy val song = playlist.lift(songIndex).getOrElse(empty)
}

trait Song {
  def url:String
}

/** TODO change BibleFile to Typeclass based or trait based implementation **/
class UniversalAudioPlayer[T <: Song](empty:T) {
  val htmlAudio = audio.render

  val state = BehaviorSubject[State[T]](State(Seq.empty, -1, empty))

  class Data {
    val position = BehaviorSubject(0.0)
    val duration = BehaviorSubject(0.0)
    val isPlayingSub = BehaviorSubject(false)
    val isPlaying = isPlayingSub.distinctUntilChanged
    val progress = position.combineLatestWith(duration)((p,d) => if (d != 0) p/d else 0)

    val playlist = state.map(_.playlist).distinctUntilChanged
    val songIndex = state.map(_.songIndex).distinctUntilChanged
    val song = state.map(_.song).distinctUntilChanged
    val history = song.scan(List.empty[T])((acc, i) => i :: acc)

    val songAndState: Observable[(T, Boolean)] = song combineLatest isPlaying
    val songAndPosition = song combineLatest position

    //-----------------------------------
    //play when state changes!
    song.subscribe(s => htmlAudio.src = s.url)
    htmlAudio.ontimeupdate = (e:Event) => {
      position.next(htmlAudio.currentTime)
      isPlayingSub.next(isPlayingNow)
    }
    htmlAudio.onplay = (e:Event) => isPlayingSub.next(true)
    htmlAudio.onpause = (e:Event) => isPlayingSub.next(false)
    htmlAudio.ondurationchange = (e:Event) => duration.next(htmlAudio.duration)
    htmlAudio.onended = (e:Event) => {
      actions.next()
      actions.play()
    }
  }

  object data extends Data

  def isPlayingNow() = {
    htmlAudio.duration > 0 && !htmlAudio.paused
  }


  class Actions {
    def stop() = {
      htmlAudio.pause()
      htmlAudio.currentTime = 0
    }

    def seek(time: Double) = {
      htmlAudio.currentTime = Math.max(0, htmlAudio.currentTime + time)
    }

    def setPosition(d: Double) = htmlAudio.currentTime = d
    def setPositionPercentage(progress: Double) = setPosition(progress * htmlAudio.duration)

    def play() = htmlAudio.play()
    def pause() = htmlAudio.pause()
    def toggle():Unit = if(htmlAudio.paused) play() else pause()

    def setSong(s:T, autoplay:Boolean = true): Unit = {
      val curr = state.getValue()
      setSongInPlaylist(curr.playlist.indexWhere(s === _), autoplay)
    }

    def toggle(s:T, playIfNew:Boolean = true, forcePositionIfNew:Double = 0):Unit = {
      val last = state.getValue()
      dom.console.warn("toggle:" + last.song)
      if(last.song.url === s.url) {
        toggle()
      } else {
        setSongInPlaylist(last.playlist.indexWhere(s === _), playIfNew).subscribe { i =>
          setPosition(forcePositionIfNew)
        }
      }
    }

    def setPlaylist(playlist:Seq[T], selectedPredicate:T => Boolean) = state.next(State(playlist, playlist.indexWhere(selectedPredicate), empty))
    def setPlaylist(playlist:Seq[T], playIndex:Int) = state.next(State(playlist, playIndex, empty))

    def prev() = {
      val s = state.getValue()
      setSongInPlaylist(0 max (s.songIndex - 1))
    }

    def next() = {
      val s = state.getValue()
      setSongInPlaylist(
        (s.songIndex + 1) % s.playlist.length
      )
    }

    def setSongInPlaylist(playIndex:Int, forcePlay:Boolean = false): Observable[State[T]] = {

      val isPlaying = isPlayingNow()
      val res = state.edit(_.copy(songIndex = playIndex))
      res.subscribe { e =>
        if (isPlaying || forcePlay) play()
      }
      res
    }
  }

  object actions extends Actions

}