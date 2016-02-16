// オプショナルな属性を持つXML要素の作り方

package playground.xml

object XmlElementWithAnOptionalAttribute extends App {
  def getElem(foo: Option[String]) = <elem foo={ foo.orNull }></elem>

  def getElem2(foo: String = null) = <elem foo={ foo }></elem>

  assert(getElem(None) == <elem></elem>)
  assert(getElem(Some("bar")) == <elem foo="bar"></elem>)

  assert(getElem2() == <elem></elem>)
  assert(getElem2("bar") == <elem foo="bar"></elem>)
}
