package playground.spray.example2.directives

import java.util.Base64

import org.scalatest.{ FreeSpec, Matchers }
import playground.spray.example2.Base64DecodingService
import spray.http.HttpEntity
import spray.http.MediaTypes._
import spray.routing.{ MalformedRequestContentRejection, UnsupportedRequestContentTypeRejection }
import spray.testkit.ScalatestRouteTest

class Base64EncodingDirectivesSpec
    extends FreeSpec
    with ScalatestRouteTest
    with Base64DecodingService
    with Matchers {
  def actorRefFactory = system // connect the DSL to the test ActorSystem

  private def encode(value: String): String = Base64.getEncoder.encodeToString(value.getBytes)

  "return decoded body" in {
    Get("/", HttpEntity(`application/base64`, encode("Say hello"))) ~> decodeRoute ~> check {
      responseAs[String] should include("Say hello")
    }
  }

  "return decoded body with end newlines" in {
    Get("/", HttpEntity(`application/base64`, encode("Say hello") + "\n\n")) ~> decodeRoute ~> check {
      responseAs[String] should include("Say hello")
    }
  }

  "return MalformedRequestContentRejection for invalid Base64 data" in {
    Get("/", HttpEntity(`application/base64`, "invalid base64 data")) ~> decodeRoute ~> check {
      rejection should be(a[MalformedRequestContentRejection])
    }
  }

  "return rejection when content-type is not application/base64" in {
    Get("/", HttpEntity(encode("Say hello"))) ~> decodeRoute ~> check {
      rejection shouldBe UnsupportedRequestContentTypeRejection("Expected 'application/base64'")
    }
  }
}
