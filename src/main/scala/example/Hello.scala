package example

import org.scalajs.dom
import dom.document
import org.scalajs.jquery.jQuery

object Hello {

  def hello(name: String) = s"Hello, $name!"

  def main(args: Array[String]): Unit = {
    println(hello("Scala in console"))
    jQuery(() => setupUI())
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  def addClickedMessage(): Unit = {
    jQuery("body").append("<p>You clicked the button!</p>")
  }

  def setupUI(): Unit = {
    jQuery("#click-me-button").click(() => addClickedMessage())
    jQuery("body").append(s"<p>${hello("Scala in page")}</p>")
  }

}
