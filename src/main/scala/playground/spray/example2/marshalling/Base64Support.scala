package playground.spray.example2.marshalling

import java.util.Base64

import spray.http.{ HttpEntity, MediaTypes }
import spray.httpx.unmarshalling.UnmarshallerLifting._
import spray.httpx.unmarshalling._

trait Base64Support {
  type Base64String = String

  implicit val Base64Unmarshaller: FromRequestUnmarshaller[Base64String] = {
    fromRequestUnmarshaller {
      fromMessageUnmarshaller {
        Unmarshaller[Base64String](MediaTypes.`application/base64`) {
          case x: HttpEntity.NonEmpty =>
            new String(Base64.getDecoder.decode(x.asString.trim), "UTF-8")
        }
      }
    }
  }
}

object Base64Support extends Base64Support
