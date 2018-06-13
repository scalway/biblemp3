package rabbi.player

import org.scalajs.dom
import org.scalajs.dom.Event
import rabbi.player.UniversalAudioPlayer.{Actions, Data}
import rxscalajs.Observable
import rxscalajs.subjects.BehaviorSubject

import scalatags.JsDom.all.audio
import rabbi.utils.Implicits._

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

  val data = new Data[Observable, T] {
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

  def isPlayingNow() = {
    htmlAudio.duration > 0 && !htmlAudio.paused
  }


  val actions = new Actions[T] {
    override def getState(): State[T] = state.getValue()

    def seek(time: Double) = {
      htmlAudio.currentTime = Math.max(0, htmlAudio.currentTime + time)
    }

    def setPosition(d: Double) = htmlAudio.currentTime = d
    def setPositionPercentage(progress: Double) = setPosition(progress * htmlAudio.duration)

    def play() = htmlAudio.play()
    def pause() = htmlAudio.pause()
    def toggle():Unit = if(htmlAudio.paused) play() else pause()

    def setPlaylist(playlist:Seq[T], selectedPredicate:T => Boolean) = state.next(State(playlist, playlist.indexWhere(selectedPredicate), empty))
    def setPlaylist(playlist:Seq[T], playIndex:Int) = state.next(State(playlist, playIndex, empty))

    def setSongInPlaylist(playIndex:Int, forcePlay:Boolean = false): Observable[State[T]] = {

      val isPlaying = isPlayingNow()
      val res = state.edit(_.copy(songIndex = playIndex))
      res.subscribe { e =>
        if (isPlaying || forcePlay) play()
      }
      res
    }
  }
}

object UniversalAudioPlayer {

  trait Data[F[_], T] {
    val position:F[Double]
    val duration:F[Double]
    val isPlaying:F[Boolean]
    val progress:F[Double]
    val playlist:F[Seq[T]]
    val songIndex:F[Int]
    val song:F[T]
    val history:F[Seq[T]]
    val songAndState:F[(T, Boolean)]
    val songAndPosition:F[(T, Double)]
  }

  trait Actions[T] {
    def getState():State[T]
    def seek(time: Double):Unit
    def setPosition(d: Double):Unit
    def setPositionPercentage(progress: Double):Unit
    def play():Unit
    def pause():Unit
    def toggle():Unit
    def setPlaylist(playlist:Seq[T], selectedPredicate:T => Boolean):Unit
    def setPlaylist(playlist:Seq[T], playIndex:Int):Unit
    def setSongInPlaylist(playIndex:Int, forcePlay:Boolean = false): Observable[State[T]]

    def toggle(s:T, playIfNew:Boolean = true, forcePositionIfNew:Double = 0):Unit = {
      val last = getState()
      dom.console.warn("toggle:" + last.song)
      if(last.song === s) {
        toggle()
      } else {
        setSongInPlaylist(last.playlist.indexWhere(s === _), playIfNew).subscribe { i =>
          setPosition(forcePositionIfNew)
        }
      }
    }

    def setSong(s:T, autoplay:Boolean = true): Unit = {
      val curr = getState()
      setSongInPlaylist(curr.playlist.indexWhere(s === _), autoplay)
    }

    def stop():Unit = {
      pause()
      setPosition(0)
    }

    def prev():Unit = {
      val s = getState()
      setSongInPlaylist(0 max (s.songIndex - 1))
    }

    def next():Unit = {
      val s = getState()
      setSongInPlaylist(
        (s.songIndex + 1) % s.playlist.length
      )
    }
  }
}