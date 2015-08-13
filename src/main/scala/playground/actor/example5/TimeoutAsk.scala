// アクターのaskでタイムアウトを超過した場合どうなるかを試せるサンプル

package playground.actor.example5

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.util.{ Success, Failure, Try }

class SlowActor extends Actor {
  override def receive: Receive = {
    case wait: Duration =>
      Thread.sleep(wait.toMillis)
      sender ! "done"
  }
}

object TimeoutAsk extends App {
  val system = ActorSystem("slow-actors")

  def testTimeout(actorLatency: Duration, askTimeout: FiniteDuration, futureTimeout: Duration): Unit = Try {
    println(s"actorLatency = $actorLatency, askTimeout = $askTimeout, futureTimeout = $futureTimeout")
    val slowActor = system.actorOf(Props(classOf[SlowActor]))
    val future = ask(slowActor, actorLatency)(Timeout(askTimeout)).mapTo[String]
    Await.result(future, futureTimeout)
  } match {
    case Failure(err) => println(s"-> failure: $err\n")
    case Success(_)   => println(s"-> success\n")
  }

  // 1. ask(?)のタイムアウトを超過する場合
  // 次のエラーが発生します。
  // akka.pattern.AskTimeoutException: Ask timed out on [...] after [1000 ms]
  testTimeout(2 second, 1 second, 3 second)

  // 2. Futureのタイムアウトを超過する場合
  // 次のエラーが発生します。
  // java.util.concurrent.TimeoutException: Futures timed out after [1 second]
  // なお、askも中断されてしまうので、`sender ! "done"` がデッドレターになります。
  testTimeout(2 second, 3 second, 1 second)

  // 3. 問題なく終了する場合
  testTimeout(2 second, 3 second, 3 second)

  system.shutdown()
}
