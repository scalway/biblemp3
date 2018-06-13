package rabbi.player

import rabbi.model.BibleFile
import rxscalajs.Observable

class AudioPlayer extends UniversalAudioPlayer(BibleFile.empty)

/**
  * allows to use shortcut notations for data and actions.
  * Not sure if needed but it'll allow us to keep Components
  * preaty clean. We could remove it later (if we find it difficult to maintain)
  * @param data ll be accessed as *
  * @param actions ll accessed as $
  */
class Shortcuts[T,E](data:T, actions:E) {
  val * = data
  val $ = actions
}

class PlayerShortcuts(player:AudioPlayer) extends Shortcuts(player.data, player.actions)
