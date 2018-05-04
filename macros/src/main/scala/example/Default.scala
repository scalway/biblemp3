package example

/* Magnolia, version 0.7.1. Copyright 2018 Jon Pretty, Propensive Ltd.
 *
 * The primary distribution site is: http://co.ntextu.al/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import magnolia.CompileTimeState.Stack
import magnolia._

import scala.language.experimental.macros
import scala.reflect.macros.{blackbox, whitebox}

/** typeclass for providing a default value for a particular type */
trait Default[T] { def default: T }

/** companion object and derivation object for [[Default]] */
object Default {

  type Typeclass[T] = Default[T]

  /** constructs a default for each parameter, using the constructor default (if provided),
    *  otherwise using a typeclass-provided default */
  def combine[T](ctx: CaseClass[Default, T]): Default[T] = new Default[T] {
    def default = ctx.construct { param =>
      param.default.getOrElse(param.typeclass.default)
    }
  }

  /** chooses which subtype to delegate to */
  def dispatch[T](ctx: SealedTrait[Default, T])(): Default[T] = new Default[T] {
    def default: T = ctx.subtypes.head.typeclass.default
  }

  /** default value for a string; the empty string */
  implicit val string: Default[String] = new Default[String] { def default = "" }

  /** default value for ints; 0 */
  implicit val int: Default[Int] = new Default[Int] { def default = 0 }

  /** default value for sequences */
  implicit def seq[A]: Default[Seq[A]] = macro {
    implicit val d:Default[A] = gen[A]
    new Typeclass[Seq[A]] {
      def default = Seq(d.default)
    }
  }

  def seqImpl[T](c0: scala.reflect.macros.blackbox.Context)
             (e2: c0.WeakTypeTag[T]): c0.Expr[Seq[T]] = {
    import c0.universe._

  }

  /** generates default instances of [[Default]] for case classes and sealed traits */
  implicit def gen[T]: Default[T] = macro Magnolia.gen[T]
}