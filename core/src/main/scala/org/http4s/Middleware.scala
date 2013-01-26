package org.http4s

object RequestRewriter {

}

object Middleware {
  def rewriteRequest(rewrite: PartialFunction[Request, Request]): Middleware = {
    route: Route => rewrite.orElse({ case req: Request => req }: RequestRewrite).andThen(route)
  }

  def service(prefix: String): Middleware = rewriteRequest {
    case req: Request if req.scriptName.startsWith(prefix) =>
      req.copy(scriptName = req.scriptName + prefix, pathInfo = req.pathInfo.substring(prefix.length))
  }

  val methodOverride: Middleware = rewriteRequest {
    // Clean this up with a request extractor
    case req: Request if req.headers.get("X-Method-Override").isDefined =>
      req.copy(requestMethod = Method(req.headers("X-Method-Override")))
  }
}



/*

*/