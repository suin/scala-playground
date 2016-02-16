// オプショナルな属性を持つXML要素の作り方

package playground.xml

import scala.xml.Text

object XmlElementWithAnOptionalAttribute extends App {
  // orElseを使う方法
  def getElem1(foo: Option[String] = None) = <elem foo={ foo.orNull }></elem>

  // nullを使う方法
  def getElem2(foo: String = null) = <elem foo={ foo }></elem>

  // Option[xml.Text]を使う方法
  def getElem3(foo: Option[Text] = None) = <elem foo={ foo }></elem>

  assert(getElem1() == <elem></elem>)
  assert(getElem1(Some("bar")) == <elem foo="bar"></elem>)

  assert(getElem2() == <elem></elem>)
  assert(getElem2("bar") == <elem foo="bar"></elem>)

  assert(getElem3() == <elem></elem>)
  assert(getElem3(Some(Text("bar"))) == <elem foo="bar"></elem>)
}
