// チャットを例に集約ルートのActorを実行時に生成する方法

package playground.actor.example2

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object ChatRoomProtocol {
  case class Join(username: String)
  case class Leave(username: String)
  case class Message(username: String, content: String)
}

// チャットルームアクター
class ChatRoom(val roomId: String) extends Actor with ActorLogging {
  import ChatRoomProtocol._

  log.info(s"Chat room initialized: roomId = $roomId")

  private var messages = Seq[Message]()
  private var members = Set[String]()

  override def receive: Receive = {
    case Join(username) =>
      members = members + username
      log.info(s"A user joined: $username. Total member: ${members.size}")
    case Leave(username) =>
      members = members - username
      log.info(s"A user left: $username. Total member: ${members.size}")
    case message: Message =>
      messages = messages :+ message
      log.info(s"Message added: @${message.username}: ${message.content}")
  }
}

// コンストラクタがあるアクターはコンパニオンオブジェクトにファクトリーメソッドを作る
object ChatRoom {
  def open(roomId: String): Props = Props(classOf[ChatRoom], roomId)
}

object ChatRoomManagerProtocol {
  case class OpenChatRoom(roomId: String)
}

// チャットルームを管理するアクター
class ChatRoomManager extends Actor with ActorLogging {
  import ChatRoomManagerProtocol._

  private var chatRooms = Map.empty[String, ActorRef]

  override def receive: Actor.Receive = {
    case OpenChatRoom(roomId) => 
      log.info(s"Opening new chat room: roomId = $roomId")
      val chatRoom = context.actorOf(ChatRoom.open(roomId), roomId)
      chatRooms = chatRooms + (roomId -> chatRoom)
      sender ! chatRoom
  }
}

object ChatModelExample extends App {
  // アクターシステムを作る
  val system: ActorSystem = ActorSystem("chat")

  // チャットルームを管理するアクターを作る
  val chatRoomManager: ActorRef = system.actorOf(Props(classOf[ChatRoomManager]), "rooms")

  // チャットルームを作る
  import ChatRoomManagerProtocol._
  implicit val timeout = Timeout(1 seconds)
  val future: Future[ActorRef] = (chatRoomManager ? OpenChatRoom("room1")).mapTo[ActorRef]

  // 作成されたチャットルームにメッセージを送信する
  import ChatRoomProtocol._
  future.onComplete {
    case Failure(error) => throw error
    case Success(room) =>
      room ! Join("alice")
      room ! Join("bob")
  }

  // 作成済みのチャットルームを探し、そこにメッセージを送信する
  val room1 = system.actorSelection("user/rooms/room1")
  room1 ! Message("alice", "Hi! bob.")
  room1 ! Message("bob", "See you!")
  room1 ! Leave("bob")

  // 存在しないチャットルーム
  val room2 = system.actorSelection("user/rooms/room2")
  room2 ! Join("carol") // デッドレターになる

  system.shutdown()
}
