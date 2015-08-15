// 子アクターが死んだら通知を受け取る例

package playground.actor.example7

import akka.actor._

class ParentActor extends Actor with ActorLogging {
  private var children = Set[ActorRef]()

  self ! "start"

  override def receive: Receive = {
    case "start" =>
      children = Set(
        context.actorOf(Props(classOf[ChildActor], "child1"), "child1"),
        context.actorOf(Props(classOf[ChildActor], "child2"), "child2"),
        context.actorOf(Props(classOf[ChildActor], "child3"), "child3")
      )
      // context.watchで対象のActorの死亡通知(Terminated)を受け取れるようになる
      children.foreach { child => context.watch(child) }
    case Terminated(child) =>
      // 監視対象のActorが死んだ時
      children = children - child
      log.info("child is dead: {}. Now children total are {}.", child.path, children.size)
    case any @ _ =>
      log.warning("unknown message: {}", any)
  }

  override def preStart(): Unit = log.info("parent is starting")

  override def postStop(): Unit = log.info("parent has been stopped")
}

class ChildActor(name: String) extends Actor with ActorLogging {
  // 毒薬を飲むと死ねる
  self ! PoisonPill

  override def receive: Receive = {
    case any @ _ => log.warning("unknown message: {}", any)
  }

  override def preStart(): Unit = log.info("{} is starting", name)

  override def postStop(): Unit = log.info("{} has been stopped", name)
}

object WatchDeathOfActors extends App {
  val system = ActorSystem("death-of-child-actor")
  val parent = system.actorOf(Props(classOf[ParentActor]), "parent")
  Thread.sleep(500L)
  system.shutdown()
}
