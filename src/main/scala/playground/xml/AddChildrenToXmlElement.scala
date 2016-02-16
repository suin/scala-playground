// XML要素に複数の子要素を追加するサンプル

package playground.xml

import scala.xml.Elem

object AddChildrenToXmlElement extends App {
  // 単純にxml.Elemのリストを受け取る場合
  def ul(items: Seq[Elem]): Elem = <ul>{ items }</ul>
  assert(
    ul(Seq(<li>item1</li>, <li>item2</li>, <li>item3</li>)) ==
      <ul><li>item1</li><li>item2</li><li>item3</li></ul>
  )
  assert(ul(Seq()) == <ul></ul>)

  // xml.Elem以外の値を受け取る場合
  def ol(items: Seq[String]): Elem = <ol>{ items.map { x => <li>{ x }</li> } }</ol>
  assert(
    ol(Seq("item1", "item2", "item3")) ==
      <ol><li>item1</li><li>item2</li><li>item3</li></ol>
  )
  assert(ol(Seq()) == <ol></ol>)
}
