package org.http4s

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.iteratee.Enumerator

class MockServer(route: Route)(implicit executor: ExecutionContext = ExecutionContext.global) {
  def apply(req: Request): Future[Response] = {
    try {
      route.lift(req).getOrElse(Future.successful(onNotFound))
    } catch {
      case t: Throwable => Future.successful(onError(t))
    }
  }

  def onNotFound: Response = Response(statusLine = StatusLine(404, "Not found"))

  def onError: PartialFunction[Throwable, Response] = {
    case e: Exception => Response(statusLine = StatusLine(500, "Internal server error"))
  }
}
