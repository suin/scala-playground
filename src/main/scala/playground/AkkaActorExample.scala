// Actorにメッセージを送信するシンプルな例

package playground

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

// アクターとやりとりするためのプロトコルを定義します。
object ActorProtocol {
  case class Message(data: String)
}

// アクタークラス
class MessageActor extends Actor {
  import ActorProtocol.Message

  private var state = Vector[String]()

  override def receive: Receive = {
    case Message(data) =>
      state = state :+ data
      println("Current state = " + state)
  }
}

object AkkaActorExample extends App {

  // アクターシステムを作ります。
  val system: ActorSystem = ActorSystem("my-actor-system")

  // アクターのインスタンスを作ります。
  // 普通のクラスならnewで作るところですが、
  // アクターはsystemに追加する必要があるので、
  // systemのファクトリーメソッドで生成します。
  // ActorRefが戻ってくるところに注目。
  val messageActor: ActorRef = system.actorOf(Props[MessageActor])

  // 必要なプロトコルをimportしておきます。
  import ActorProtocol.Message

  // アクターにメッセージを送信します。
  messageActor ! Message("Hello")
  messageActor ! Message("Hello")
  messageActor ! Message("Hello")

  system.shutdown()
}
