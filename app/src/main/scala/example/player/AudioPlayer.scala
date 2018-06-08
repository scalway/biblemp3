package example.player

import example.model.BibleFile

class AudioPlayer extends UniversalAudioPlayer(BibleFile.empty)

trait PlayerShortcuts {
  def player:AudioPlayer
  val * = player.data
  val $ = player.actions
}
