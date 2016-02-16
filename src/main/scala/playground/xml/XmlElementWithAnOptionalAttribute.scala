// オプショナルな属性を持つXML要素の作り方

package playground.xml

object XmlElementWithAnOptionalAttribute extends App {
  def getElem(foo: Option[String]) = <elem foo={ foo.orNull }></elem>

  assert(getElem(None) == <elem></elem>)
  assert(getElem(Some("bar")) == <elem foo="bar"></elem>)
}
