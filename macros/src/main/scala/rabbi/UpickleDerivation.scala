package rabbi

import magnolia._
import ujson.{ObjVisitor, Visitor}
import upickle.{Api, AttributeTagged}

import scala.language.experimental.macros
import scala.reflect.ClassTag

trait UpickleSupportCommon {
  type Typeclass[T]
  type CC[T] = CaseClass[Typeclass, T]
  type ST[T] = SealedTrait[Typeclass, T]
  protected val upickleApi0:Api

  private def key_@(arg:Seq[Any]) = arg.collectFirst {
    case x:upickle.key => x.s
  }

  /**
    * TODO make all methods protected! We cannot do that due to UpickleDerivationMixin but
    * it is deprecated and probably would be deleted in near future!
    */
  def ccName[T](ctx:CC[T]) = {
    key_@(ctx.annotations).getOrElse(ctx.typeName.full)
  }

  def paramLabel[F[_], E](arg:Param[F, E]) = {
    key_@(arg.annotations).getOrElse(arg.label)
  }

  def dispatch0[T](ctx: ST[T]) = ctx.subtypes.map(_.typeclass)
}

trait UpickleSupportR extends UpickleSupportCommon {
  import upickleApi0._
  type Typeclass[T] <: Reader[T]

  def deriveCaseRTagged[T](ctx:CC[T]) = {
    new TaggedReader.Leaf[T](ccName[T](ctx), deriveCaseR(ctx))
  }

  def deriveCaseR[T](ctx:CC[T]) = JsObjR.map[T] { r =>
    ctx.construct(p => {
      r.value.get(paramLabel(p)) match {
        case Some(x) => readJs(x)(p.typeclass)
        case None => p.default.get
      }
    })
  }
}

trait UpickleSupportW extends UpickleSupportCommon {
  import upickleApi0._
  type Typeclass[T] <: Writer[T]

  def deriveCaseWTagged[T:ClassTag](ctx:CC[T]) = {
    new TaggedWriter.Leaf[T](implicitly, ccName[T](ctx), deriveCaseW(ctx))
  }

  def deriveCaseW[T](ctx:CC[T]) = new CaseW[T] {
    override def writeToObject[R](ww: ObjVisitor[_, R], v: T): Unit = {
      ctx.parameters.zipWithIndex.foreach { case (arg, i) =>
        val argWriter = arg.typeclass
        val value = arg.dereference(v)
        if (!arg.default.contains(value)) {
          ww.visitKey(objectAttributeKeyWriteMap(paramLabel(arg)), -1)
          ww.visitValue(
            argWriter.write(
              ww.subVisitor.asInstanceOf[Visitor[Any, Nothing]],
              value
            ),
            -1
          )
        }
      }
    }
  }
}

class UpickleDerivationW[A <: Api](override val upickleApi0:Api) extends UpickleSupportW {
  import upickleApi0._
  type Typeclass[T] = Writer[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = deriveCaseWTagged(ctx)
  def dispatch[T](ctx: ST[T]): Typeclass[T] = Writer.merge[T](dispatch0(ctx) :_*)
  implicit def genW[T]: Typeclass[T] = macro Magnolia.gen[T]
}

class UpickleDerivationR[A <: Api](override val upickleApi0:A) extends UpickleSupportR {
  import upickleApi0._
  type Typeclass[T] = Reader[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = deriveCaseRTagged(ctx)
  def dispatch[T](ctx: ST[T]): Typeclass[T] = Reader.merge[T](dispatch0(ctx) :_*)
  implicit def genR[T]: Typeclass[T] = macro Magnolia.gen[T]
}

class UpickleDerivation[A <: Api](override val upickleApi0:A) extends UpickleSupportW with UpickleSupportR {
  import upickleApi0._
  type Typeclass[T] = ReadWriter[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = ReadWriter.join[T](deriveCaseRTagged(ctx), deriveCaseWTagged(ctx))
  def dispatch[T](ctx: ST[T]): Typeclass[T] =  ReadWriter.merge[T](dispatch0(ctx) :_*)
  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

@deprecated(
  """use with caution! It is only reason why we have public fields in Support classes and they leaks with import.""".stripMargin, "0.1")
trait UpickleDerivationMixin { self:Api =>
  type Typeclass[T] = ReadWriter[T]

  protected val deriveSupport = new UpickleSupportW with UpickleSupportR {
    val upickleApi0:self.type = self
    type Typeclass[T] = upickleApi0.Typeclass[T]
  }

  import deriveSupport._

  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = ReadWriter.join[T](deriveCaseRTagged(ctx), deriveCaseWTagged(ctx))
  def dispatch[T](ctx: ST[T]): Typeclass[T] = ReadWriter.merge[T](dispatch0(ctx) :_*)
  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

//-----------------------------------------------------------------
//                 HOW TO USE IT
//-----------------------------------------------------------------

/**
  * Preffered way of using it is create special object in your Api class that'll bring
  * deriviation to your application when imported.
  *
  * {{{
  *   import UpApi._
  *   import UpApi.derive._
  *
  *   implicit val msgRW = implicitly[ReadWriter[Message]]
  * }}}
  *
  * if `Message` is recursive in more complicated way use lazy val instead!
  *
  * {{{
  *   implicit lazy val msgRW = implicitly[ReadWriter[Message]]
  * }}}
  *
  * */
object UpApi extends AttributeTagged {
  val derive = new UpickleDerivation(this)
}

/**
  * If you need only Reader or Writer You can create just instance that creates it.
  *
  * {{{
  *   import UpApi._
  *   import UpApi.deriveW._
  *
  *   implicit val msgW = implicitly[Writer[Message]]
  * }}}
  * */
object UpApi2 extends AttributeTagged {
  val deriveW = new UpickleDerivationW(this)
  val deriveR = new UpickleDerivationR(this)
}

/**
  * You can also simply create derivation for existing api and use it with companion to it
  *
  * {{{
  *   import upickle.default._
  *   import UpDerive._
  *
  *   implicit val msgRW = implicitly[ReadWriter[Message]]
  * }}}
  */
object UpDerive extends UpickleDerivation(upickle.default)

/**
  * You can use UpickleDerivationMixin to create own api with automatic
  * deriviation turned on by default. It is risky and should be avoided i guess
  * because it can create so much garbage for you... but you can.
  *
  * {{{
  *   import UpApiAuto._
  *
  *   implicit val msgRW = implicitly[ReadWriter[Message]]
  * }}}
  * */
object UpApiAuto extends AttributeTagged with UpickleDerivationMixin {}
