package rabbi

import magnolia._
import ujson.{ObjVisitor, Visitor}
import upickle.core.Types
import upickle.{Api, AttributeTagged, core}

import scala.language.experimental.macros
import scala.reflect.ClassTag

trait UpickleSupportCommon[A <: Api] {
  type Typeclass[T]
  type CC[T] = CaseClass[Typeclass, T]
  type ST[T] = SealedTrait[Typeclass, T]
  protected val api:A

  private def key_@(arg:Seq[Any]) = arg.collectFirst {
    case x:upickle.key => x.s
  }

  def ccName[T](ctx:CC[T]) = {
    key_@(ctx.annotations).getOrElse(ctx.typeName.full)
  }

  def paramLabel[F[_], E](arg:Param[F, E]) = {
    key_@(arg.annotations).getOrElse(arg.label)
  }

  def dispatch0[T](ctx: ST[T]) = ctx.subtypes.map(_.typeclass)
}

trait UpickleSupportR[A <: Api] extends UpickleSupportCommon[A] {
  import api._
  type Typeclass[T] <: Reader[T]

  def caseRTagged[T](ctx:CC[T]) = {
    new TaggedReader.Leaf[T](ccName[T](ctx), caseR(ctx))
  }

  def caseR[T](ctx:CC[T]) = JsObjR.map[T] { r =>
    ctx.construct(p => {
      r.value.get(paramLabel(p)) match {
        case Some(x) => readJs(x)(p.typeclass)
        case None => p.default.get
      }
    })
  }
}

trait UpickleSupportW[A <: Api] extends UpickleSupportCommon[A] {
  import api._
  type Typeclass[T] <: Writer[T]

  def caseWTagged[T:ClassTag](ctx:CC[T]) = {
    new TaggedWriter.Leaf[T](implicitly, ccName[T](ctx), caseW(ctx))
  }

  def caseW[T](ctx:CC[T]) = new CaseW[T] {
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

class UpickleDerivationW[A <: Api](val api:A) extends UpickleSupportW[A] {
  import api._
  type Typeclass[T] = Writer[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = caseWTagged(ctx)
  def dispatch[T](ctx: ST[T]): Typeclass[T] = Writer.merge[T](dispatch0(ctx) :_*)
  implicit def genW[T]: Typeclass[T] = macro Magnolia.gen[T]
}

class UpickleDerivationR[A <: Api](val api:A) extends UpickleSupportR[A] {
  import api._
  type Typeclass[T] = Reader[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = caseRTagged(ctx)
  def dispatch[T](ctx: ST[T]): Typeclass[T] = Reader.merge[T](dispatch0(ctx) :_*)
  implicit def genR[T]: Typeclass[T] = macro Magnolia.gen[T]
}

class UpickleDerivation[A <: Api](val api:A) extends UpickleSupportW[A] with UpickleSupportR[A] {
  import api._
  type Typeclass[T] = ReadWriter[T]
  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = ReadWriter.join[T](caseRTagged(ctx), caseWTagged(ctx))
  def dispatch[T](ctx: ST[T]): Typeclass[T] =  ReadWriter.merge[T](dispatch0(ctx) :_*)
  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

trait UpickleDerivationMixin { self:Api =>
  type Typeclass[T] = ReadWriter[T]

  protected val ds = new UpickleSupportW[Api] with UpickleSupportR[Api] {
    val api:self.type = self
    type Typeclass[T] = api.Typeclass[T]
  }

  import ds._


  def combine[T:ClassTag](ctx: CC[T]): Typeclass[T] = ReadWriter.join[T](caseRTagged(ctx), caseWTagged(ctx))
  def dispatch[T](ctx: ST[T]): Typeclass[T] = ReadWriter.merge[T](dispatch0(ctx) :_*)
  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

object DefaultUpickleMW extends UpickleDerivationW(upickle.default)
object DefaultUpickleMR extends UpickleDerivationR(upickle.default)
object DefaultUpickleM  extends UpickleDerivation (upickle.default)

object UpApi extends AttributeTagged with UpickleDerivationMixin {

}

class UpApiAuto extends AttributeTagged {
  val derive = new UpickleDerivation(this)
}

object UpApiAuto extends UpApiAuto

object DefaultUpickleFull  extends UpickleDerivation (upickle.default)