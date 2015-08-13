// アクターでログを出力する方法

package playground.actor.example3

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}

// `ActorLogging`をインポートすることで`log` APIが追加される
class MyActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case message: String =>
      // ログのレベルは application.conf 等で設定できます。
      // @see http://doc.akka.io/docs/akka/snapshot/general/configuration.html

      // DEBUGレベル
      log.debug("メッセージを受信しました")
      log.debug("メッセージを受信しました: {}", message) // テンプレートを使うことも可能

      // INFOレベル
      log.info("メッセージを受信しました")
      log.info("メッセージを受信しました: {}", message)

      // WARNレベル
      log.warning("メッセージを受信しました")
      log.warning("メッセージを受信しました: {}", message)

      // ERRORレベル
      log.error("メッセージを受信しました")
      log.error("メッセージを受信しました: {}", message)
  }
}

object ActorLoggingExample extends App {
  val system = ActorSystem("actor-logging-example")
  val actor = system.actorOf(Props(classOf[MyActor]))
  actor ! "こんにちは！"
  system.shutdown()
}
