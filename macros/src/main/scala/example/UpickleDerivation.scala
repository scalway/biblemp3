package example

import magnolia._
import shapeless.Lazy
import ujson.{Js, ObjVisitor, Visitor}
import upickle.default._

import scala.language.experimental.macros
import scala.reflect.ClassTag

object UpickleDerivation {
  type Typeclass[T] = ReadWriter[T]


  def combine[T:ClassTag](ctx: CaseClass[ReadWriter, T]): ReadWriter[T] = {

    val CaseWriterT = new CaseW[T] {
      override def writeToObject[R](ww: ObjVisitor[_, R], v: T): Unit = ctx.parameters.zipWithIndex.foreach { case (arg, i) =>
        ww.visitKey(objectAttributeKeyWriteMap(arg.label), -1)
        val w = arg.typeclass
        ww.visitValue(
          w.write(
            ww.subVisitor.asInstanceOf[Visitor[Any, Nothing]],
            arg.dereference(v)
          ),
          -1
        )
      }
    }
    ReadWriter.join[T](
      new TaggedReader.Leaf[T](ctx.typeName.full, JsObjR.map[T] { r =>
        ctx.construct(p => readJs(r.value(p.label))(p.typeclass))
      }),

      new TaggedWriter.Leaf[T](implicitly, ctx.typeName.full, CaseWriterT)
    )
  }

  def dispatch[T](ctx: SealedTrait[ReadWriter, T]): ReadWriter[T] =  {
    ctx.subtypes.foreach(s => println(s.typeName))
    val col = ctx.subtypes.map(_.typeclass)
    ReadWriter.merge[T](col :_*)
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}

object UpickleDerivationR {
  type Typeclass[T] = Reader[T]

  def combine[T:ClassTag](ctx: CaseClass[Reader, T]): Reader[T] = {
    new TaggedReader.Leaf[T](ctx.typeName.full, JsObjR.map[T] { r =>
      ctx.construct(p => readJs(r.value(p.label))(p.typeclass))
    })
  }

  def dispatch[T](ctx: SealedTrait[Writer, T]): Writer[T] =  {
    ctx.subtypes.foreach(s => println(s.typeName))
    val col = ctx.subtypes.map(_.typeclass)
    Writer.merge[T](col :_*)
  }

  implicit def gen[T]: Writer[T] = macro Magnolia.gen[T]
}


object UpickleDerivationW {
  type Typeclass[T] = Writer[T]

  def combine[T:ClassTag](ctx: CaseClass[Writer, T]): Writer[T] = {

    val CaseWriterT = new CaseW[T] {
      override def writeToObject[R](ww: ObjVisitor[_, R], v: T): Unit = ctx.parameters.zipWithIndex.foreach { case (arg, i) =>
        ww.visitKey(objectAttributeKeyWriteMap(arg.label), -1)
        val w = arg.typeclass
        ww.visitValue(
          w.write(
            ww.subVisitor.asInstanceOf[Visitor[Any, Nothing]],
            arg.dereference(v)
          ),
          -1
        )
      }
    }

    new TaggedWriter.Leaf[T](implicitly, ctx.typeName.full, CaseWriterT)
  }

  def dispatch[T](ctx: SealedTrait[Writer, T]): Writer[T] =  {
    ctx.subtypes.foreach(s => println(s.typeName))
    val col = ctx.subtypes.map(_.typeclass)
    Writer.merge[T](col :_*)
  }

  implicit def gen[T]: Writer[T] = macro Magnolia.gen[T]
}

