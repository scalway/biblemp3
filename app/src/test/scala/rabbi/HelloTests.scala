package rabbi

import org.scalajs.dom
import utest._

object HelloTests extends TestSuite {
  val tests = Tests {
    "Hello.world" - {
      Hello.hello("Scala")
      val body = dom.document.body.innerHTML
      assert(body == "Hello, Scala!")
    }
  }
}
