// SprayでServer-Sent Eventsを実装するサンプル
package playground.spray.example1

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.joda.time.DateTime
import spray.can.Http
import spray.http.CacheDirectives.`no-cache`
import spray.http.HttpCharsets.`UTF-8`
import spray.http.HttpHeaders.`Cache-Control`
import spray.http.{ ChunkedMessageEnd, ChunkedResponseStart, ContentType, HttpEntity, HttpResponse, HttpResponsePart, MediaType, MediaTypes, MessageChunk }
import spray.routing.HttpService

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

// ServerSentEventsStreamerにイベント送信などを命令するためのプロトコル
object ServerSentEventsStreamerProtocol {
  case class SendEvent(id: Option[Long] = None, event: Option[String] = None, data: String)
  case object StopStreaming
}

// Server-Sent Eventsのレスポンスを送信する抽象クラス
class ServerSentEventsStreamer(client: ActorRef) extends Actor with ActorLogging {
  import ServerSentEventsStreamerProtocol._

  private object StartStreaming
  private trait ClientStatus
  private object ClientReady extends ClientStatus
  private object ClientBusy extends ClientStatus
  private object SendBlank

  private val `text/event-stream` = MediaTypes.register(MediaType.custom("text/event-stream"))
  private val EOL = "\n"

  private var clientStatus: ClientStatus = ClientReady
  private var goingToStop = false
  private val messageQueue = mutable.Queue[HttpResponsePart]()

  self ! StartStreaming

  // Some proxy servers drop HTTP connections after a short timeout.
  // To protect against such proxy servers, send blank every 15 seconds.
  private val timer = context.system.scheduler.schedule(15 seconds, 15 seconds, self, SendBlank)

  final override def receive: Receive = {
    case StartStreaming =>
      log.info("[SSE] start streaming")
      val streamStart = ":" + (" " * 2048) + EOL + EOL // 2 kB padding for IE
      val responseStart = HttpResponse(
        entity = HttpEntity(ContentType(`text/event-stream`, `UTF-8`), streamStart),
        headers = List(`Cache-Control`(`no-cache`))
      )
      sendOrEnqueue(ChunkedResponseStart(responseStart))

    case event: SendEvent =>
      log.info("[SSE] received event: {}", event)
      sendOrEnqueue(MessageChunk(stringifyEvent(event)))

    case ClientReady =>
      if (goingToStop && messageQueue.isEmpty) {
        stopStreaming()
      }

      if (messageQueue.nonEmpty) {
        val message = messageQueue.dequeue()
        log.info("[SSE] dequeue and send message, as client is ready: {}", message)
        sendMessageNow(message)
      } else {
        log.info("[SSE] client is ready")
        clientStatus = ClientReady
      }

    case SendBlank =>
      log.info("[SSE] send blank")
      sendOrEnqueue(MessageChunk(":" + EOL + EOL))

    case StopStreaming =>
      log.info("[SSE] going to stop streaming")
      sendOrEnqueue(ChunkedMessageEnd)
      goingToStop = true

    case x: Http.ConnectionClosed =>
      log.info("[SSE] HTTP connection was closed")
      stopStreaming()

    case other @ _ => receiveCommand(other)
  }

  protected def receiveCommand: Receive = PartialFunction.empty

  private def sendOrEnqueue(message: HttpResponsePart): Unit = {
    if (clientStatus == ClientReady) {
      log.info("[SSE] send message right now, as client is ready: {}", message)
      sendMessageNow(message)
    } else {
      log.info("[SSE] enqueue message, as client is busy: {}", message)
      messageQueue.enqueue(message)
    }
  }

  private def sendMessageNow(message: HttpResponsePart): Unit = {
    client ! message.withAck(ClientReady)
    clientStatus = ClientBusy
  }

  private def stringifyEvent(event: SendEvent): String = Seq[String](
    event.id.map("id:" + _).getOrElse(""),
    event.event.map("event:" + _).getOrElse(""),
    event.data.split(EOL).map("data:" + _).mkString(EOL)
  ).filter(_.nonEmpty).mkString(EOL) + EOL + EOL

  private def stopStreaming(): Unit = {
    log.info("[SSE] streamer stopped")
    timer.cancel()
    self ! PoisonPill
  }
}

class CurrentTimeStreamer(client: ActorRef) extends ServerSentEventsStreamer(client) {
  import ServerSentEventsStreamerProtocol._
  private val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, "time")

  override protected def receiveCommand: Receive = {
    case "time" =>
      val now = DateTime.now
      self ! SendEvent(
        id = Some(now.getMillis),
        data = now.toString
      )
  }

  override def postStop(): Unit = {
    super.postStop()
    timer.cancel()
  }
}

class MyHttpServiceActor extends Actor with MyHttpService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

trait MyHttpService extends HttpService {
  val myRoute = get { context =>
    // contextを受け取り、context.responderをSSEStreamerに渡す
    actorRefFactory.actorOf(Props(
      classOf[CurrentTimeStreamer],
      context.responder
    ))
  }
}

object ServerSentEventsExample extends App {
  implicit val system = ActorSystem("sse-example")
  val service = system.actorOf(Props[MyHttpServiceActor], "my-http")
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 9000)
}
