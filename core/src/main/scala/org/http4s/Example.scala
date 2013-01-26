package org.http4s

import Middleware._

object Example {
  val route =
    service("/") {
      service("/demo") {
        methodOverride {
          case req => Handler { Response(statusLine = StatusLine(200, "OK")) }
        }
      }
    }
}