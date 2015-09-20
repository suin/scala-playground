package playground.spray.example2.directives

import playground.spray.example2.marshalling.Base64Support
import spray.routing.Directive1
import spray.routing.directives.BasicDirectives._
import spray.routing.directives.MarshallingDirectives._

object Base64EncodingDirectives extends Base64EncodingDirectives

trait Base64EncodingDirectives extends Base64Support {
  def decodeBase64Request: Directive1[String] = {
    entity(as[Base64String]).flatMap(provide)
  }
}