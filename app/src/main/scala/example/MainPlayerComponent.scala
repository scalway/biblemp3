package example

import example.model.{Bible, BibleFile}
import example.player.AudioPlayerDatabaseIntegration
import example.player.view.AudioPlayerView
import example.utils.Database
import example.utils.Implicits._
import example.views.player.BibleFileSelectorView
import org.scalajs.dom.Event

import scalatags.JsDom.all._

class MainPlayerComponent {
  val audioPlayer = new player.AudioPlayer()
  val audioPlayerView = new AudioPlayerView(audioPlayer)

  val apDb = new AudioPlayerDatabaseIntegration(audioPlayer)
  apDb.init(Bible.all.files)

  val audioPlayerPrinter = new player.AudioPlayerDebug(audioPlayer)

  val bibleList = new BibleFileSelectorView(audioPlayer)
  bibleList.init()
  bibleList.connectWithDatabaseTimes(Database.position.stream)

  audioPlayerView.metaContainerView.onclick = { (e:Event) =>
    val song = audioPlayer.state.getValue().song
    bibleList.show(song)
  }

  val view = div(
    div(id := "stickyMenu",
      audioPlayerView.view,
      bibleList.tabs.headerView
    ),

    bibleList.tabs.contentView,

    footer(
      id := "footer",
      p("Biblia-mp3 2018")
    )
  ).render

  object actions {
    def setSong(s:BibleFile) = {
      audioPlayer.actions.setSong(s, false)
      bibleList.ot.show(s)
      bibleList.nt.show(s)
    }
  }

}
