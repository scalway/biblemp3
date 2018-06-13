package rabbi.views.player

import rabbi.model.{Bible, BibleFile}
import rabbi.player.AudioPlayer
import rabbi.utils.bootstrap.BTabs
import rabbi.views.InfoView

import scalatags.JsDom.all._
import rabbi.utils.Implicits._
import rxscalajs.Observable

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

  def init() = {
    var oldSong = BibleFile.empty

    audioPlayer.data.songAndState.subscribe { (t:(BibleFile, Boolean)) =>
      val current = t._1

      allBookFiles.collect { case x if x.isPlaying(oldSong) => x.setPlaying(None) }
      allBookFiles.collect { case x if x.isPlaying(current) => x.setPlaying(Some(t._2)) }
      oldSong = current
    }
  }

  def setTime(file:BibleFile, time:Double) = {
    allBookFiles.find(file === _.file).foreach(_.setProgress(file.progressOf(time)))

    //set gray bar to notify that book was started!
    allBooks.find(_.containsBook(file)).foreach(_.header.markAsStarted(time > 0))
  }

  /** TODO why we need it? It is just trivial binding */
  def connectWithDatabaseTimes(db:Observable[(BibleFile, Double)]) = {
    db.subscribe { t =>
      val (file, time) = t
      setTime(file, time)
    }
  }

  def show(song: BibleFile) = {
    ot.show(song)
    nt.show(song)
  }
}
