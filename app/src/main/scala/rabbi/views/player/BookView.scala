package rabbi.views.player

import rabbi.model.{BibleFile, Book}
import rabbi.player.AudioPlayer
import rabbi.utils.Bootstrap
import rabbi.utils.Implicits._

import scalatags.JsDom.all._

case class BookView(b:Book, color:String, player:AudioPlayer) {
  val fileViews = b.files.map(s => new BibleFileView(s, player))
  val header = new BibleHeaderView(b, color)

  val chapters = div(
    id:="chapter-" + b.short.hashCode,
    fileViews.map(_.view)
  ).render

  val view = div(Bootstrap.collapse(header.view, chapters)).render

  def containsBook(file:BibleFile) = fileViews.exists(_.file === file)
}

