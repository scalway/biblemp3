package example.player

import example.model.BibleFile
import org.scalajs.dom.Event
import rxscalajs.subjects.BehaviorSubject

import scalatags.JsDom.all._
import example.utils.Implicits._
import rxscalajs.subscription.Subscription

case class State(playlist:Seq[BibleFile], songIndex:Int) {
  lazy val song = playlist.lift(songIndex).getOrElse(BibleFile.empty)
}

/** TODO change BibleFile to Typeclass based implementation **/
class AudioPlayer {
  val htmlAudio = audio.render

  val state = BehaviorSubject[State](State(Seq.empty, -1))

  object data {
    val position = BehaviorSubject(0.0)
    val duration = BehaviorSubject(0.0)
    val isPlaying = BehaviorSubject(false)

    val progress = position.combineLatestWith(duration)((p,d) => if (d != 0) p/d else 0)
    val playlist = state.map(_.playlist).distinct
    val songIndex = state.map(_.songIndex).distinct
    val song = state.map(_.song)

    //-----------------------------------
    //play when state changes!
    song.subscribe(s => htmlAudio.src = s.url)
    htmlAudio.ontimeupdate = (e:Event) => {
      position.next(htmlAudio.currentTime)
      isPlaying.next(isPlayingNow)
    }
    htmlAudio.onplay = (e:Event) => isPlaying.next(true)
    htmlAudio.onpause = (e:Event) => isPlaying.next(false)
    htmlAudio.ondurationchange = (e:Event) => duration.next(htmlAudio.duration)
    htmlAudio.onended = (e:Event) => {
      actions.next()
      actions.play()
    }
  }

  def isPlayingNow() = {
    htmlAudio.duration > 0 && !htmlAudio.paused
  }

  object actions {
    def stop() = {
      htmlAudio.pause()
      htmlAudio.currentTime = 0
    }

    def seek(time: Double) = {
      htmlAudio.currentTime = Math.max(0, htmlAudio.currentTime + time)
    }

    def setPositionPercentage(progress: Double) = htmlAudio.currentTime = progress * htmlAudio.duration

    def play() = htmlAudio.play()
    def pause() = htmlAudio.pause()
    def toggle():Unit = if(htmlAudio.paused) htmlAudio.play() else htmlAudio.pause()

    def toggle(s:BibleFile, playIfNew:Boolean = true):Unit = {
      val curr = state.getValue()
      if(curr.song === s) toggle() else {
        actions.setSongInPlaylist(curr.playlist.indexWhere(s === _), playIfNew)
      }
    }

    def setPlaylist(playlist:Seq[BibleFile], playIndex:Int) = {
      state.next(State(playlist, playIndex))
    }
    def prev() = {
      val s = state.getValue()
      actions.setSongInPlaylist(0 max (s.songIndex - 1))
    }

    def next() = {
      val s = state.getValue()
      actions.setSongInPlaylist(
        (s.songIndex + 1) % s.playlist.length
      )
    }

    def setSongInPlaylist(playIndex:Int, forcePlay:Boolean = false): Subscription = {
      val isPlaying = isPlayingNow
      val res = state.edit(_.copy(songIndex = playIndex))
      res.subscribe { e =>
        if (isPlaying || forcePlay) play()
      }
    }
  }

}


trait PlayerShortcuts {
  def player:AudioPlayer
  val * = player.data
  val $ = player.actions
}
