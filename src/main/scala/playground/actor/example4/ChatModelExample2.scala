// ChatModelExampleに表明を加えたバージョンです

package playground.actor.example4

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

object ChatRoomProtocol {
  case class Join(username: String)
  case class Leave(username: String)
  case class Message(username: String, content: String)

  sealed trait Response
  case object RoomIsFull extends Response
  case object OK extends Response
}

// チャットルームアクター
class ChatRoom(val roomId: String, val capacity: Int) extends Actor with ActorLogging {
  import ChatRoomProtocol._

  log.info("Room opened. Capacity is {}", capacity)

  private var messages = Seq[Message]()
  private var members = Set[String]()

  override def receive: Receive = {
    case Join(username) =>
      if (members.size >= capacity) {
        sender ! RoomIsFull
        log.info("{} couldn't join since this room is full", username)
      } else {
        members = members + username
        sender ! OK
        log.info("{} joined. Room member is now {}", username, members)
      }
    case Leave(username) =>
      members = members - username
      log.info("{} left. Room member is now {}", username, members)
    case message: Message =>
      messages = messages :+ message
      log.info("@{}: {}", message.username, message.content)
  }
}

object ChatRoom {
  def open(roomId: String, capacity: Int): Props = Props(classOf[ChatRoom], roomId, capacity)
}

object ChatRoomManagerProtocol {
  case class OpenChatRoom(roomId: String, capacity: Int)
}

// チャットルームを管理するアクター
class ChatRoomManager extends Actor with ActorLogging {
  import ChatRoomManagerProtocol._

  private var chatRooms = Map.empty[String, ActorRef]

  override def receive: Actor.Receive = {
    case OpenChatRoom(roomId, capacity) =>
      log.info("Opening new chat room: {}", roomId)
      val chatRoom = context.actorOf(ChatRoom.open(roomId, capacity), roomId)
      chatRooms = chatRooms + (roomId -> chatRoom)
      sender ! chatRoom
  }
}

object ChatModelExample2 extends App {
  val system: ActorSystem = ActorSystem("chat")
  val chatRoomManager: ActorRef = system.actorOf(Props(classOf[ChatRoomManager]), "rooms")

  import ChatRoomManagerProtocol._
  implicit val timeout = Timeout(1 seconds)
  val future: Future[ActorRef] = (chatRoomManager ? OpenChatRoom("room1", capacity = 2)).mapTo[ActorRef]
  val room = Await.result(future, 1 second)

  import ChatRoomProtocol._

  def awaitForResponse(future: Future[Response]): Response = Await.result(future, 1 second)

  val future1 = (room ? Join("alice")).mapTo[Response]
  awaitForResponse(future1) match {
    case RoomIsFull => println("alice couldn't join")
    case OK         => println("alice could join")
  }

  val future2 = (room ? Join("bob")).mapTo[Response]
  awaitForResponse(future2) match {
    case RoomIsFull => println("bob couldn't join")
    case OK         => println("bob could join")
  }

  val future3 = (room ? Join("carol")).mapTo[Response]
  awaitForResponse(future3) match {
    case RoomIsFull => println("carol couldn't join")
    case OK         => println("carol could join")
  }

  system.shutdown()
}
