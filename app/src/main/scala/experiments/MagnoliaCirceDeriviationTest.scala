package experiments

import io.circe.Decoder.Result
import io.circe.magnolia.derivation.decoder.semiauto.Typeclass
import io.circe.magnolia.derivation.encoder.semiauto.Typeclass
import io.circe._

import scala.scalajs.js.annotation.JSExport

sealed trait Msg {}

case class Hello(text:String) extends Msg
case class ByeBye(text:String) extends Msg
case class Multi(msgs:Seq[Msg]) extends Msg

@JSExport
class MagnoliaCirceDeriviationTest {
  @JSExport
  def main(): Unit = {
    import io.circe.magnolia.derivation.decoder.semiauto._
    import io.circe.magnolia.derivation.encoder.semiauto._

    class ProxyEncoder[A](val over: () => Encoder[A]) extends Encoder[A] {
      override def apply(a: A) = over().apply(a)
    }

    class ProxyDecoder[A](f: () => Decoder[A]) extends Decoder[A] {
      override def apply(c: HCursor): Result[A] = f().apply(c)
    }

    implicit val encoder: Encoder[Msg] = {
      implicit var res:Encoder[Msg] = null
      implicit lazy val proxy = new ProxyEncoder[Msg](() => res)
      res = deriveMagnoliaEncoder[Msg]
      res
    }

    implicit val decoder: Decoder[Msg] = {
      implicit var res:Decoder[Msg] = null
      implicit lazy val proxy = new ProxyDecoder[Msg](() => res)
      res = deriveMagnoliaDecoder[Msg]
      res
    }

    import  io.circe.syntax._

    println(
      (Multi(Seq(Hello("test1"))):Msg).asJson.noSpaces
    )
  }
}
