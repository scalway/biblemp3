package example

import example.model.BibleFile
import example.utils.Database
import example.views.AudioPlayerView
import org.scalajs.dom

object AudioPlayer {
  /** (old, new, isPlaying) => */
  type SongListener = (Option[BibleFile], BibleFile, Boolean) => Any

  var songListeners:Seq[SongListener] = Seq.empty

  def addChangeSongListener(s:SongListener) = songListeners :+= s

  var last:Option[BibleFile] = None
  var lastIsPlaying:Boolean = false

  def autoShouldPlay(s:BibleFile) = last match {
    case Some(l) if l == s => !lastIsPlaying
    case other => true
  }

  def toggle(s:BibleFile)(shouldPlay:Boolean = autoShouldPlay(s)):Unit = {
    setBibleFile(s)(shouldPlay)
  }

  def setBibleFile(s:BibleFile)(play:Boolean = autoShouldPlay(s)) = {
    Database.lastItemUrl.set(s.url)
    onSongChange(s, play)
    hideCover()
    AudioPlayerView.play(s, Database.position.get(s).filter(_ < s.timeReal - 4).getOrElse(0), play)
    lastIsPlaying = !lastIsPlaying
  }

  def hideCover() = {
    dom.window.setTimeout(() => AudioPlayerView.logoHover.style.display = "none", 1000)
    AudioPlayerView.logoHover.style.opacity = "0.0"
  }


  def onSongChange(to:BibleFile, isPlaying:Boolean) = {
    lastIsPlaying = isPlaying
    dom.console.warn(s"playing $to")
    songListeners.foreach(_.apply(last, to, isPlaying))
    last = Some(to)
  }

  def pause() = AudioPlayerView.pause()
}