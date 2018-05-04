package example.model
import example.{BibleMp3Data, UpickleDerivation}
import example.utils.Implicits._
import ujson.Transformable
import upickle.default



object Test {
  sealed trait Tree
  case class Node(child:Seq[Tree], age:Int) extends Tree
  case class Leaf(s:String) extends Tree
}

//object Test3 {
//  import Test._
//  import UpickleDerivation._
//  import upickle.default._
//  println(write[T](C("Test")))
//}

object Test2 {
  import Test._
//  import example.UpickleDerivation._
//  import magnolia._

//  import upickle.default._

  //import upickle.default.{SeqLikeReader => _, SeqLikeWriter => _, _}

//  implicit def seqRW[T](implicit r:ReadWriter[T]): default.ReadWriter[Seq[T]] = ReadWriter.join(
//    upickle.default.SeqLikeReader[Seq, T](r, implicitly),
//    upickle.default.SeqLikeWriter[Seq, T](r)
//  )
//  implicit val NRW = implicitly[ReadWriter[Tree]]
//  implicit lazy val seqTree = SeqLikeReader[Seq, Tree](c, implicitly)
//  implicit val c = gen[Tree]
//
//  private val write1 = write[Tree](Node(Seq(Leaf("dd"))), 2)
//  println(write1)

  import example.Default._
  println(gen[Tree].default)

//  println(read[Tree](write1))

//  Test3.toString
}



case class Bible(nt:BibleTestament, ot:BibleTestament) {
  def all = BibleTestament("", ot.files ++ nt.files)
}

object Bible extends Bible(
  nt = BibleTestament("Nowy Testament", BibleMp3Data.NT),
  ot = BibleTestament("Stary Testament", BibleMp3Data.OT)
) {
  Test2.toString
}

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