package org

import play.api.libs.iteratee.Iteratee

package object http4s {
  type Handler = Iteratee[Array[Byte], Response]
  type Route = PartialFunction[Request, Handler]

  type Middleware = Route => Route
  type RequestRewrite = PartialFunction[Request, Request]
}