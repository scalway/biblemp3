package example.utils

import collection.mutable.{LinkedHashMap, LinkedHashSet, Map => MutableMap}
import scala.collection.mutable



object Implicits {
  class GroupByOrderedImplicitImpl[A](val t: Traversable[A]) extends AnyVal {
    def groupByOrdered[K](f: A => K): MutableMap[K, LinkedHashSet[A]] = {
      val map = LinkedHashMap[K,LinkedHashSet[A]]().withDefault(_ => LinkedHashSet[A]())
      for (i <- t) {
        val key = f(i)
        map(key) = map(key) + i
      }
      map
    }
  }

  implicit def traversableToTrOps[T](t: Traversable[T]): GroupByOrderedImplicitImpl[T] =
    new GroupByOrderedImplicitImpl(t)
}
