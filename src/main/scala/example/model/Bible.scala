package example.model
import example.BibleMp3Data
import example.utils.Implicits._

case class Bible(nt:BibleTestament, ot:BibleTestament) {
  def all = BibleTestament("", ot.files ++ nt.files)
}

object Bible extends Bible(
  BibleTestament("Stary Testament", BibleMp3Data.OT),
  BibleTestament("Nowy Testament", BibleMp3Data.NT)
)

case class BibleTestament(name:String, files:Seq[BibleFile]) {
  val books:Seq[Book] = files.groupByOrdered(_.shortBook).map {
    case (n, f) =>
      val head = f.head
      Book(head.book, head.shortBook, head.bookKind, f.toSeq)
  }.toSeq
}

case class Book(name:String, short:String, group:String , files:Seq[BibleFile]) {
  def getVersion: Map[String, Seq[BibleFile]] = {
    val res = files.groupBy(s => s.version)
    res.withDefault(c => res("cała"))
  }

  def fullVersion = getVersion("cała")
  def partedVersion = getVersion("podzielona")
}

case class BibleFile(
                      url:String,
                      book:String,
                      shortBook:String,
                      version:String,
                      versionPartName:String,
                      name:String,
                      bookKind:String
                    )

object BibleFile {
  val empty: BibleFile = BibleFile("","","","","","","")
}