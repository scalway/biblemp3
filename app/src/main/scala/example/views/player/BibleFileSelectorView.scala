package example.views.player

import example.model.{Bible, BibleFile}
import example.player.AudioPlayer
import example.utils.Database
import example.utils.bootstrap.BTabs
import example.views.InfoView

import scalatags.JsDom.all._
import example.utils.Implicits._

class BibleFileSelectorView(audioPlayer:AudioPlayer) {
  val colorsST = Seq("#e00b3c", "#9a13dd", "#1357dd", "#13ddae", "#13b5dd")
  val colorsNT = Seq("#ddac25", "#6113dd", "#13b5dd", "#d7dd13")

  val nt = new BibleTestamentView(Bible.nt, colorsNT, audioPlayer)
  val ot = new BibleTestamentView(Bible.ot, colorsST, audioPlayer)

  val info = new InfoView() {
    val icon = Seq(
      padding := "1.18em 0",
      span(cls := "fa fa-info-circle fa-2x")
    )
  }

  val tabs = new BTabs {
    val stTab   = tab("Stary Testament", ot.view)
    val ntTab   = tab("Nowy Testament", nt.view)
    val infoTab = tab(info.icon, div(id := "app-info", cls := "tab-pane", info.view).render)
    //ugly :(
    infoTab.headerView.classList.add("infoTab")

  }

  val allBooks = nt.booksViews ++ ot.booksViews
  val allBookFiles = allBooks.flatMap(_.fileViews)

  def connectWithDatabase() = {
    Database.position.stream.subscribe { t =>
      val (file, time) = t
      setTime(file, time)
    }
  }

  def init() = {
    var oldSong = BibleFile.empty

    audioPlayer.data.playingState.subscribe { (t:(BibleFile, Boolean)) =>
      val current = t._1

      allBookFiles.collect { case x if x.isPlaying(oldSong) => x.setPlaying(None) }
      allBookFiles.collect { case x if x.isPlaying(current) => x.setPlaying(Some(t._2)) }
      oldSong = current
    }

    connectWithDatabase()
    nt.view.classList.add("active")
  }

  def setTime(file:BibleFile, time:Double) = {
    allBookFiles.find(file === _.file).foreach(_.setProgress(file.progressOf(time)))

    //set gray bar to notify that book was started!
    allBooks.find(_.containsBook(file)).foreach(_.header.markAsStarted(time > 0))
  }

  def show(song: BibleFile) = {
    ot.show(song)
    nt.show(song)
  }
}
