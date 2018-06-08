package example.player

import example.model.{Bible, BibleFile}
import example.utils.Database
import example.utils.Implicits._

class AudioPlayerDatabaseIntegration(val player:AudioPlayer) extends PlayerShortcuts {
  def init(playlist: Seq[BibleFile]) = {
    val lastUrl = Database.lastItemUrl()
    val last = playlist.find(_.url === lastUrl).getOrElse(BibleFile.empty)
    val lastIdx = playlist.indexOf(last)
    player.actions.setPlaylist(Bible.all.files, lastIdx)
    player.actions.setPosition(Database.position(last))

    (*.song combineLatest  *.position).subscribe { d =>
      val (s, p) = d
      Database.position.set(s, p)
    }

    *.song.map(_.url).subscribe(Database.lastItemUrl.set(_))
  }



}
