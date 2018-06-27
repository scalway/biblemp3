package experiments

import io.circe.Decoder.Result
import io.circe._

import scala.scalajs.js.annotation.JSExport

object MagnoliaCirceDeriviationTest  {
  sealed trait Msg {}

  case class Hello(text:String) extends Msg
  case class ByeBye(text:String) extends Msg
  case class Multi(msgs:Seq[Msg]) extends Msg
}

@JSExport
class MagnoliaCirceDeriviationTest {
  import MagnoliaCirceDeriviationTest._

  @JSExport
  def main(): Unit = {
    import io.circe.magnolia.derivation.decoder.semiauto._
    import io.circe.magnolia.derivation.encoder.semiauto._

    implicit lazy val encoder: Encoder[Msg] = deriveMagnoliaEncoder[Msg]
    implicit lazy val decoder: Decoder[Msg] = deriveMagnoliaDecoder[Msg]

    import io.circe.syntax._

    println(
      (Multi(Seq(Hello("test1"))):Msg).asJson.noSpaces
    )
  }
}
