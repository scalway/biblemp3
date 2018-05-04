package example

import utest._

object HelloTests extends TestSuite {
  val tests = Tests {
    "Hello.world" - {
      val result = Hello.hello("Scala")
      assert( result == "Hello, Scala!" )
    }
  }
}
