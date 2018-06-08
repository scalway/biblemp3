package example

import example.model.{Bible, BibleFile}
import example.player.AudioPlayerDatabaseIntegration
import example.player.view.AudioPlayerView
import example.utils.Database
import example.views.{BibleTestamentView, BibleViews, InfoView}
import org.scalajs.dom
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._
import example.utils.Implicits._
import org.scalajs.dom.Event

@JSExportTopLevel("example.Hello")
object Hello {

  def hello(name:String) = {
    val msg = s"Hello, $name!"
    dom.document.body.innerHTML = msg
    msg
  }

  @JSExport
  def main(args: Array[String] = Array.empty): Unit = {

    val audioPlayer = new player.AudioPlayer()
    val audioPlayerPrinter = new player.AudioPlayerDebug(audioPlayer)
    val audioPlayerView = new AudioPlayerView(audioPlayer)

    val apDb = new AudioPlayerDatabaseIntegration(audioPlayer)
    apDb.init(Bible.all.files)

    val ntView = new BibleTestamentView(Bible.nt, colorsNT, audioPlayer)
    val otView = new BibleTestamentView(Bible.ot, colorsST, audioPlayer)
    ntView.view.classList.add("active")

    var oldSong = BibleFile.empty
    val allBookFiles = ntView.booksViews.flatMap(_.fileViews) ++ otView.booksViews.flatMap(_.fileViews)
    (audioPlayer.data.song combineLatest audioPlayer.data.isPlaying).subscribe { (t:(BibleFile, Boolean)) =>
      val data = t._1

      allBookFiles.collect { case x if x.file.url == oldSong.url => x.setPlaying(None) }
      allBookFiles.collect { case x if x.file.url == data.url => x.setPlaying(Some(t._2)) }
      oldSong = data
    }

    Database.position.stream.subscribe { t =>
      val (b, time) = t
      allBookFiles.find(b === _.file).map(_.view.style.setProperty("--proc", 100 * b.progressOf(time)  + "%"))

    }

    audioPlayerView.metaContainerView.onclick = { (e:Event) =>
      val song = audioPlayer.state.getValue().song
      otView.show(song)
      ntView.show(song)
    }


    //read last item
    dom.window.setTimeout({ () =>
      Database.lastItemUrl.get().flatMap { url =>
        Bible.all.files.find(_.url === url)
      }.map {  s =>
        audioPlayer.actions.setSong(s, false)
        otView.show(s)
        ntView.show(s)
      }
    },400)

    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(
      createView(
        audioPlayerView.view,
        ntView.view,
        otView.view
      )
    )

    Database.position.init(Bible.all.files)
  }

  val colorsST = Seq("#e00b3c", "#9a13dd", "#1357dd", "#13ddae", "#13b5dd")
  val colorsNT = Seq("#ddac25", "#6113dd", "#13b5dd", "#d7dd13")

  val header: Div = div().render
  val footer: Div = div(
    id := "footer",
    p("Biblia-mp3 2018")
  ).render



  def createView(player:HTMLElement, ntView:HTMLElement, otView:HTMLElement): Div = div(
    header,
    div(id := "stickyMenu",
      player,
      new BibleViews().view
    ),
    div(cls := "tab-content",
      ntView,
      otView,
      div(id := "app-info", cls := "tab-pane", new InfoView().view)
    ),
    footer
  ).render
}



