package experiments

import upickle.key

import scala.scalajs.js.annotation.JSExport

object MagnoliaUpickleDeriviationTest  {
  sealed trait Msg {}

  @key("Hello") case class Hello( @key("txt") text:Option[String] = None) extends Msg
  @key("ByeBye") case class ByeBye(@key("map") text:Map[String, Int] = Map.empty) extends Msg
  @key("Multi") case class Multi(@key("msgs123") msgs:Seq[Msg]) extends Msg

  case class Envelope(msg:Msg)
}

@JSExport
class MagnoliaUpickleDeriviationTest {
  import MagnoliaUpickleDeriviationTest._

  @JSExport
  def main(): Unit = {
    import rabbi.UpApi._
    import rabbi.UpApi.derive.readWriterOf

    //implicit lazy val msgRW = readWriterOf[Msg]
    implicit lazy val envelopeRW: ReadWriter[Envelope] = readWriterOf[Envelope]


    val obj = Envelope(Multi(List(Hello(Some("test1")),ByeBye(), Hello(), ByeBye(Map("Dupa" -> 2)))))
    val str = write(obj, 2)
    println(str)
    val obj2 = read[Envelope](str)
    println(obj)
    val same = obj2 == obj
    println(obj2 + " same:" + same)
  }
}
