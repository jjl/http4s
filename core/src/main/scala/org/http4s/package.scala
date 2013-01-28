package org

import play.api.libs.iteratee.Iteratee
import concurrent.{ExecutionContext, Future}

package object http4s {
  type Route = PartialFunction[Request, Future[Response]]

  type Middleware = (Route => Route)

  type RequestRewriter = PartialFunction[Request, Request]

  def rewriteRequest(f: RequestRewriter): Middleware = {
    route: Route => f.orElse({ case req: Request => req }: RequestRewriter).andThen(route)
  }

  type ResponseTransformer = PartialFunction[Response, Response]

  def transformResponse(f: ResponseTransformer)(implicit executor: ExecutionContext): Middleware = {
    route: Route => route andThen { _.map(f) }
  }
}