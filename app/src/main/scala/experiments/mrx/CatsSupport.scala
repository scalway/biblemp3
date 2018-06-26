package experiments.mrx

import cats.{Monad, Semigroup}
import mhtml.{Rx, Var}

trait CatsSupport {
    implicit val mhtmlRxMonadIntstance: Monad[Rx] =
      new Monad[Rx] {
        def pure[A](x: A): Rx[A] =
          Rx(x)

        def flatMap[A, B](fa: Rx[A])(f: A => Rx[B]): Rx[B] =
          fa.flatMap(f)

        def tailRecM[A, B](a: A)(f: A => Rx[Either[A, B]]): Rx[B] =
          flatMap(f(a)) {
            case Right(b) => pure(b)
            case Left(nextA) => tailRecM(nextA)(f)
          }

        override def product[A, B](a: Rx[A], b: Rx[B]): Rx[(A, B)] =
          a.zip(b)
      }

    implicit def mhtmlRxSemigroupIntstance[A]: Semigroup[Rx[A]] =
      new Semigroup[Rx[A]] {
        def combine(x: Rx[A], y: Rx[A]): Rx[A] = x.merge(y)
      }

    implicit def mhtmlVarSyntaxSemigroup[A](fa: Var[A]) =
      new _root_.cats.syntax.SemigroupOps[Rx[A]](fa)(mhtmlRxSemigroupIntstance)

}
