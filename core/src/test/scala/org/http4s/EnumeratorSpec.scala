package org.http4s

import play.api.libs.iteratee._

import scala.concurrent._
import scala.concurrent.duration._
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions

/**
 * @author ross
 */
class EnumeratorSpec extends Specification with NoTimeConversions {
  "Enumerating a stream" should {
    "not heat your house" in {
      val sum = Enumerator.enumerate(Stream.from(1)).through(Enumeratee.take(5)).run(Iteratee.fold(0){ _ + _})
      assert(Await.result(sum, 5 seconds) === 15)
    }
  }
}
