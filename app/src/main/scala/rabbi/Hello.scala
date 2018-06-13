package rabbi

import rabbi.model.{Bible, BibleFile}
import rabbi.player.AudioPlayerDatabaseIntegration
import rabbi.player.view.AudioPlayerView
import rabbi.utils.Database
import rabbi.views.InfoView
import org.scalajs.dom
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._
import rabbi.utils.Implicits._
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
    val biblePlayer = new MainPlayerComponent()
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(biblePlayer.view)

    //read last item
    dom.window.setTimeout({ () =>
      Database.lastItemUrl.get().flatMap { url =>
        Bible.all.files.find(_.url === url)
      }.map(biblePlayer.actions.setSong)
    },400)


    Database.position.init(Bible.all.files)
  }
}



