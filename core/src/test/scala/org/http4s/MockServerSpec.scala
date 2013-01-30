package org.http4s

import scala.language.reflectiveCalls

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import org.specs2.mutable.Specification
import play.api.libs.iteratee._
import org.specs2.time.NoTimeConversions

class MockServerSpec extends Specification with NoTimeConversions {
  import scala.concurrent.ExecutionContext.Implicits.global

  val server = new MockServer({
    case req if req.requestMethod == Method.Post && req.pathInfo == "/echo" =>
      Future.successful(Response(entityBody = req.messageBody))
    case req if req.requestMethod == Method.Post && req.pathInfo == "/sum" =>
      val it = Enumeratee.collect[Chunk] { case chunk: BodyChunk => new String(chunk.bytes).toInt }
        .transform(Iteratee.fold(0)((sum, i) => sum + i))
      req.messageBody.enumerate.run(it).map { sum => Response(entityBody = MessageBody(sum)) }
    case req if req.pathInfo == "/fail" =>
      sys.error("FAIL")
  })

  def response(req: Request, reqBody: MessageBody = MessageBody.Empty): Response = {
    Await.result(server(req.copy(messageBody = reqBody)), 5 seconds)
  }

  "A mock server" should {
    "handle matching routes" in {
      val req = Request(requestMethod = Method.Post, pathInfo = "/echo")
      val reqBody = MessageBody("one", "two", "three")
      Await.result(response(req, reqBody).entityBody.asString, 5 seconds) should_==("onetwothree")
    }

    "runs a sum" in {
      val req = Request(requestMethod = Method.Post, pathInfo = "/sum")
      val reqBody = MessageBody(1, 2, 3)
      Await.result(response(req, reqBody).entityBody.asString, 5 seconds) should_==("6")
    }

    "fall through to not found" in {
      val req = Request(pathInfo = "/bielefield")
      response(req).statusLine should_== StatusLine.NotFound
    }

    "handle exceptions" in {
      val req = Request(pathInfo = "/fail")
      response(req).statusLine should_== StatusLine.InternalServerError
    }
  }
}
