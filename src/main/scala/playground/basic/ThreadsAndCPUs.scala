// ScalaがデフォルトでCPUの数しかスレッドを作らないことを確認できるサンプル
package playground.basic

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

object ThreadsAndCPUs extends App {
  val cpus = Runtime.getRuntime.availableProcessors

  def threadTest(threads: Int): Unit = {
    println(s"CPUの数 = $cpus")
    println(s"作るスレッド数 = $threads")

    val startedOn = System.currentTimeMillis()

    // 1秒かかるスレッドをNこ作る
    val futures = Future.sequence((1 to threads).map(_ => Future {
      Thread.sleep(1000L)
    }))

    // 全てのFutureが完了するまで待つ
    Await.result(futures, Duration.Inf)
    println(s"かかった時間: ${(System.currentTimeMillis() - startedOn) / 1000}秒")
    println("---------------------------------------")
  }

  // CPUより少ないスレッド数
  threadTest(1)

  // CPUと同じスレッド数
  threadTest(cpus)

  // CPUの2倍のスレッド数
  threadTest(cpus * 2)

  // CPUの4倍のスレッド数
  threadTest(cpus * 4)
}

// 出力例
//
// CPUの数 = 8
// 作るスレッド数 = 1
// かかった時間: 1秒
// ---------------------------------------
// CPUの数 = 8
// 作るスレッド数 = 8
// かかった時間: 1秒
// ---------------------------------------
// CPUの数 = 8
// 作るスレッド数 = 16
// かかった時間: 2秒
// ---------------------------------------
// CPUの数 = 8
// 作るスレッド数 = 32
// かかった時間: 4秒
// ---------------------------------------

// ちなみに ./activator -Dscala.concurrent.context.maxThreads=1
// のように起動するとスレッド数を少なくすることができます。
