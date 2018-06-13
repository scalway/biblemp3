package rabbi.player

import rabbi.model.{Bible, BibleFile}
import rabbi.utils.Database
import rabbi.utils.Implicits._

class AudioPlayerDatabaseIntegration(val player:AudioPlayer) extends PlayerShortcuts(player) {
  def init(playlist: Seq[BibleFile]) = {
    val last = Database.lastItemUrl.get() flatMap { url => playlist.find(_.url === url) }
    val lastIdx = last.map(playlist.indexOf).getOrElse(Bible.all.ntIndex)

    player.actions.setPlaylist(Bible.all.files, lastIdx)
    last.foreach { l =>
      player.actions.setPosition(Database.position(l))
    }

    *.songAndPosition.subscribe { d =>
      val (file, position) = d
      Database.position.set(file, position)
    }

    *.song.map(_.url).subscribe(Database.lastItemUrl.set(_))
  }



}
