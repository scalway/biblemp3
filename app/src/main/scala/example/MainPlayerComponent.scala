package example

import example.model.{Bible, BibleFile}
import example.player.AudioPlayerDatabaseIntegration
import example.player.view.AudioPlayerView
import example.utils.Database
import example.utils.Implicits._
import example.views.player.BibleViews
import org.scalajs.dom.Event
import rxscalajs.Observable

import scalatags.JsDom.all._

class MainPlayerComponent {
  val audioPlayer = new player.AudioPlayer()
  val audioPlayerView = new AudioPlayerView(audioPlayer)

  val apDb = new AudioPlayerDatabaseIntegration(audioPlayer)
  apDb.init(Bible.all.files)

  val audioPlayerPrinter = new player.AudioPlayerDebug(audioPlayer)

  val bibleView = new BibleViews(audioPlayer)

  var oldSong = BibleFile.empty



  private val playingState: Observable[(BibleFile, Boolean)] =
    audioPlayer.data.song combineLatest audioPlayer.data.isPlaying

  def init() = {
    val allBooks = bibleView.nt.booksViews ++ bibleView.ot.booksViews
    val allBookFiles = allBooks.flatMap(_.fileViews)

    playingState.subscribe { (t:(BibleFile, Boolean)) =>
      val current = t._1

      allBookFiles.collect { case x if x.isPlaying(oldSong) => x.setPlaying(None) }
      allBookFiles.collect { case x if x.isPlaying(current) => x.setPlaying(Some(t._2)) }
      oldSong = current
    }

    Database.position.stream.subscribe { t =>
      val (file, time) = t
      allBookFiles.find(file === _.file).foreach(_.setProgress(file.progressOf(time)))

      //set gray bar to notify that book was started!
      allBooks.find(_.containsBook(file)).foreach(_.header.markAsStarted(time > 0))
    }
  }





  audioPlayerView.metaContainerView.onclick = { (e:Event) =>
    val song = audioPlayer.state.getValue().song
    bibleView.ot.show(song)
    bibleView.nt.show(song)
  }

  val view = div(
    div(id := "stickyMenu",
      audioPlayerView.view,
      bibleView.tabs.headerView
    ),

    bibleView.tabs.contentView,

    footer(
      id := "footer",
      p("Biblia-mp3 2018")
    )
  ).render

  object actions {
    def setSong(s:BibleFile) = {
      audioPlayer.actions.setSong(s, false)
      bibleView.ot.show(s)
      bibleView.nt.show(s)
    }
  }

}
