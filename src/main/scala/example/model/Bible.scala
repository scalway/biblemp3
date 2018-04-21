package example.model

import example.{BibleFile, BibleMp3Data}

case class Bible(nt:BibleT, ot:BibleT) {
  def all = BibleT(ot.files ++ nt.files)
}

object Bible extends Bible(
  BibleT(BibleMp3Data.OT),
  BibleT(BibleMp3Data.NT)
)

case class BibleT(files:Seq[BibleFile]) {
  def books:Seq[Book] = files.groupBy(_.shortBook).map{
    case (n, files) =>
      val head = files.head
      Book(head.book, head.shortBook, files)
  }.toSeq
}

case class Book(name:String, short:String, files:Seq[BibleFile]) {
  def parts = {
    val res = files.groupBy(_.version)
    res.withDefault(c => res.get("cała"))
  }
}