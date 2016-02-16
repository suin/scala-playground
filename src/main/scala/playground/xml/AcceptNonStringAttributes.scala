// XML要素の属性にString以外を使う方法

package playground.xml

import scala.util.matching.Regex
import scala.xml.{ Text, Elem }

object AcceptNonStringAttributes extends App {
  import Conversion._ // 暗黙の方変換を使う
  def input(size: Int, min: Double, pattern: Regex): Elem =
    <input type="number" size={ size } min={ min } pattern={ pattern }/>

  assert(
    input(size = 10, min = 1.5, pattern = "^[A-Z]{3}$".r) ==
      <input type="number" size="10" min="1.5" pattern="^[A-Z]{3}$"/>
  )
}

object Conversion {
  implicit def intToText(a: Int): Text = Text(a.toString)
  implicit def floatToText(a: Double): Text = Text(a.toString)
  implicit def regexToText(a: Regex): Text = Text(a.toString)
}
