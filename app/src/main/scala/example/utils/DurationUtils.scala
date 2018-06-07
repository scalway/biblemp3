package example.utils

import org.scalajs.dom

import scala.scalajs.js.Date

object DurationUtils {
  def preetyDuration(s1:Double) = {
    val h = Math.floor(s1 / 3600)
    val s2 = s1 % 3600
    val m = Math.floor(s2 / 60)
    val s = s2 % 60
    f"$h%02.0f:$m%02.0f:$s%02.0f"
  }
}
