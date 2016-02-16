// XML要素に動的に属性を追加する

package playground.xml

import scala.xml.{ Elem, Attribute, Null }

object AddDynamicallyAttributesToXmlElement extends App {
  // 一つの属性を動的に追加する
  def div(key: String, value: String): Elem = <div/> % Attribute("", key, value, Null)
  assert(div("class", "container") == <div class="container"/>)

  // 複数の属性を動的に追加する
  def img(attributes: (String, String)*): Elem = {
    // foldLeftを使い、Seq[(String, String)]をAttributeにしつつ畳こむ
    (<img/> /: attributes) {
      case (elem, (key, value)) => elem % Attribute("", key, value, Null)
    }
  }
  assert(img("src" -> "scala.png", "width" -> "100") == <img src="scala.png" width="100"/>)

  // Elemを拡張してfoldLeftを隠蔽しておくと可読性が高い
  implicit class ElemExtension(val elem: Elem) {
    def %(attributes: Seq[(String, String)]): Elem =
      (elem /: attributes) {
        case (e, (key, value)) => e % Attribute("", key, value, Null)
      }
  }
  def input(attributes: (String, String)*): Elem = <input type="text"/> % attributes
  assert(input("size" -> "16", "maxlength" -> "32") == <input type="text" size="16" maxlength="32"/>)
}
