package org.http4s

import scala.language.reflectiveCalls

import java.util.concurrent.TimeUnit
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.io.Codec

import org.specs2.mutable.Specification
import play.api.libs.iteratee._

class MockServerSpec extends Specification {
  import scala.concurrent.ExecutionContext.Implicits.global

  val server = new MockServer({
    case req if req.requestMethod == Method.Post && req.pathInfo == "/echo" =>
      Future.successful(Response(entityBody = req.entityBody))
    case req if req.pathInfo == "/fail" =>
      sys.error("FAIL")
  })

  "A mock server" should {
    "handle matching routes" in {
      val req = Request(requestMethod = Method.Post, pathInfo = "/echo")
      val reqBody = Enumerator("one", "two", "three").through(Enumeratee.map(Codec.toUTF8)).through(Enumeratee.map(Chunk.chunk(_)))
      Await.result(for {
        res <- server(req.copy(entityBody = reqBody))
        resBytes <- res.entityBody.run(Enumeratee.map[Chunk](_.bytes).transform(Iteratee.consume[Array[Byte]](): Iteratee[Array[Byte], Array[Byte]]))
        resString = new String(resBytes)
      } yield {
        resString should_==("onetwothree")
      }, Duration(5, TimeUnit.SECONDS))
    }

    "fall through to not found" in {
      val req = Request(pathInfo = "/bielefield")
      val reqBody = Enumerator.eof[Chunk]
      Await.result(for {
        res <- server(req)
      } yield {
        res.statusLine.code should_==(404)
      }, Duration(5, TimeUnit.SECONDS))

    }

    "handle exceptions" in {
      val req = Request(pathInfo = "/fail")
      val reqBody = Enumerator.eof[Chunk]
      Await.result(for {
        res <- server(req)
      } yield {
        res.statusLine.code should_==(500)
      }, Duration(5, TimeUnit.SECONDS))
    }
  }
}
