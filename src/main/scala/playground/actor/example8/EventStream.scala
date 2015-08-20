// Event Streamを使ったメッセージング
package playground.actor.example8

import akka.actor._

class Publisher extends Actor with ActorLogging {
  override def receive: Receive = {
    case Publish(message) =>
      log.info("Publish: {}", message)
      // Publishする
      context.system.eventStream.publish(message)
    case any @ _ =>
      log.warning("Unknown message: {}", any)
  }

  override def preStart(): Unit = log.info("Actor is starting")
}

class Subscriber extends Actor with ActorLogging {
  override def receive: Receive = {
    case Text(message) =>
      log.info("Received text: {}", message)
    case Image(filename) =>
      log.info("Received image: {}", filename)
    case any @ _ =>
      log.warning("Unknown message: {}", any)
  }

  override def preStart(): Unit = {
    log.info("Actor is starting")
    // 起動時にSubscribeするメッセージを指定する
    context.system.eventStream.subscribe(self, classOf[Message])
  }

  override def postStop(): Unit = {
    log.info("Actor has stopped")
  }
}

case class Publish(content: Message)

trait Message
case class Text(message: String) extends Message
case class Image(filename: String) extends Message

object EventStream extends App {
  val system = ActorSystem("example")

  val publisher = system.actorOf(Props(classOf[Publisher]), "publisher")
  val subscriber = system.actorOf(Props(classOf[Subscriber]), "subscriber")

  publisher ! Publish(Text("Hello"))
  publisher ! Publish(Image("smile.png"))
  Thread.sleep(10L)

  // Subscriberが死ぬと、PublisherのSubscriberリストから除外される
  subscriber ! PoisonPill
  Thread.sleep(10L)

  // これはSubscriberに届かず、デッドレターにもならない(デッドレターはタイミング次第でなる場合がある)
  publisher ! Publish(Text("Hello again"))
  Thread.sleep(10L)

  system.shutdown()
}

// example output:
// [INFO] [08/20/2015 23:01:17.481] [example-akka.actor.default-dispatcher-3] [akka://example/user/publisher] Actor is starting
// [INFO] [08/20/2015 23:01:17.481] [example-akka.actor.default-dispatcher-4] [akka://example/user/subscriber] Actor is starting
// [INFO] [08/20/2015 23:01:17.482] [example-akka.actor.default-dispatcher-3] [akka://example/user/publisher] Publish: Text(Hello)
// [INFO] [08/20/2015 23:01:17.482] [example-akka.actor.default-dispatcher-4] [akka://example/user/subscriber] Received text: Hello
// [INFO] [08/20/2015 23:01:17.482] [example-akka.actor.default-dispatcher-3] [akka://example/user/publisher] Publish: Image(smile.png)
// [INFO] [08/20/2015 23:01:17.482] [example-akka.actor.default-dispatcher-2] [akka://example/user/subscriber] Received image: smile.png
// [INFO] [08/20/2015 23:01:17.496] [example-akka.actor.default-dispatcher-4] [akka://example/user/subscriber] Actor has stopped
// [INFO] [08/20/2015 23:01:17.506] [example-akka.actor.default-dispatcher-2] [akka://example/user/publisher] Publish: Text(Hello again)
