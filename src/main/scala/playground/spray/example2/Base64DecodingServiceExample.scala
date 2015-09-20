// spray-routingでBase64エンコードされたHTTPリクエストをデコードするサンプル

package playground.spray.example2

import akka.actor.{ Actor, ActorSystem, Props }
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import playground.spray.example2.marshalling.Base64Support
import spray.can.Http
import spray.routing.HttpService

import scala.concurrent.duration._

trait Base64DecodingService extends HttpService with Base64Support {
  val decodeRoute = {
    entity(as[Base64String]) { body =>
      complete(body)
    }
  }
}

class MyHttpServiceActor extends Actor with Base64DecodingService {
  def actorRefFactory = context
  def receive = runRoute(decodeRoute)
}

object Base64DecodingServiceExample extends App {
  implicit val system = ActorSystem("base64-decoding")
  val service = system.actorOf(Props[MyHttpServiceActor], "my-http")
  implicit val timeout = Timeout(5 seconds)
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 9000)
  /*

  http://localhost:9000にアクセスすると試せます。

  ```
  $ echo -n "Hello world" | base64 | http -v localhost:9000 Content-Type:application/base64 'Accept:*'
  POST / HTTP/1.1
  Accept: *
  Accept-Encoding: gzip, deflate
  Connection: keep-alive
  Content-Length: 17
  Content-Type: application/base64
  Host: localhost:9000
  User-Agent: HTTPie/0.8.0

  SGVsbG8gd29ybGQ=

  HTTP/1.1 200 OK
  Content-Length: 11
  Content-Type: text/plain; charset=UTF-8
  Date: Sun, 20 Sep 2015 04:33:35 GMT
  Server: spray-can/1.3.3

  Hello world
  ```

  */
}
