// もしアクターが未知のメッセージを受信したらどうなるか？
package playground.actor.example6

import akka.actor.{ ActorLogging, Props, ActorSystem, Actor }

class IntOnlyActor1 extends Actor with ActorLogging {
  override def receive: Receive = {
    case number: Int => log.info("received number {}", number)
  }
}

class IntOnlyActor2 extends Actor with ActorLogging {
  override def receive: Receive = {
    case number: Int => log.info("received number {}", number)
    case any @ _     => log.warning("received unknown message: {}", any)
  }
}

object UnknownMessage extends App {
  val system = ActorSystem("unknown-message")

  // Intにしか対応していないアクターです。
  val actor1 = system.actorOf(Props(classOf[IntOnlyActor1]), "actor1")

  // これはIntなので扱えるメッセージです。
  actor1 ! 10

  // Intしか受け取らないアクターに対して文字列を送るどうなるか？
  // → 結果的に捨てられます。
  actor1 ! "Can you handle this?"

  // 未知のメッセージに対応したアクターです。
  val actor2 = system.actorOf(Props(classOf[IntOnlyActor2]), "actor2")
  actor2 ! "Can you handle this?"

  system.shutdown()
}
