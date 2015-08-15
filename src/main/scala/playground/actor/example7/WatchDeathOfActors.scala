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

// 出力されるログの例
// [INFO] [08/15/2015 22:05:06.228] [death-of-child-actor-akka.actor.default-dispatcher-3] [akka://death-of-child-actor/user/parent] parent is starting
// [INFO] [08/15/2015 22:05:06.230] [death-of-child-actor-akka.actor.default-dispatcher-2] [akka://death-of-child-actor/user/parent/child1] child1 is starting
// [INFO] [08/15/2015 22:05:06.230] [death-of-child-actor-akka.actor.default-dispatcher-5] [akka://death-of-child-actor/user/parent/child2] child2 is starting
// [INFO] [08/15/2015 22:05:06.230] [death-of-child-actor-akka.actor.default-dispatcher-6] [akka://death-of-child-actor/user/parent/child3] child3 is starting
// [INFO] [08/15/2015 22:05:06.233] [death-of-child-actor-akka.actor.default-dispatcher-6] [akka://death-of-child-actor/user/parent/child3] child3 has been stopped
// [INFO] [08/15/2015 22:05:06.233] [death-of-child-actor-akka.actor.default-dispatcher-2] [akka://death-of-child-actor/user/parent/child1] child1 has been stopped
// [INFO] [08/15/2015 22:05:06.233] [death-of-child-actor-akka.actor.default-dispatcher-5] [akka://death-of-child-actor/user/parent/child2] child2 has been stopped
// [INFO] [08/15/2015 22:05:06.237] [death-of-child-actor-akka.actor.default-dispatcher-3] [akka://death-of-child-actor/user/parent] child is dead: akka://death-of-child-actor/user/parent/child3. Now children total are 2.
// [INFO] [08/15/2015 22:05:06.238] [death-of-child-actor-akka.actor.default-dispatcher-3] [akka://death-of-child-actor/user/parent] child is dead: akka://death-of-child-actor/user/parent/child2. Now children total are 1.
// [INFO] [08/15/2015 22:05:06.238] [death-of-child-actor-akka.actor.default-dispatcher-3] [akka://death-of-child-actor/user/parent] child is dead: akka://death-of-child-actor/user/parent/child1. Now children total are 0.
// [INFO] [08/15/2015 22:05:06.731] [death-of-child-actor-akka.actor.default-dispatcher-3] [akka://death-of-child-actor/user/parent] parent has been stopped
