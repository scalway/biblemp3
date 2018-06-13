package rabbi

import org.scalajs.dom
import rabbi.model.Bible
import rabbi.utils.Database
import rabbi.utils.Implicits._

import scala.scalajs.js.annotation.JSExport

@JSExport
object RabbiPlayer {

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



