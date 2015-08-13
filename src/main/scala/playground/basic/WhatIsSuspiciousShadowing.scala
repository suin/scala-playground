// 安定識別子パターン (Stable Identifier Patterns)
// 詳しくは http://www29.atwiki.jp/tmiya/pages/109.html の「8.1.5 安定識別子パターン (Stable Identifier Patterns)」を参照。

package playground.basic

object WhatIsSuspiciousShadowing extends App {
  def doSomething(x: Int, y: Int): Unit = y match {
    case `x` => println("A") // x == y の判定になる
    case 0   => println("B")
    case 1   => println("C")
  }

  doSomething(1, 0) // => B
  doSomething(0, 1) // => C
  doSomething(1, 1) // => A
  doSomething(0, 0) // => A
}
