package org.http4s

import play.api.libs.iteratee._

import scalaz._, Scalaz._, MonadPartialOrder._
import iteratee._, iteratee.Iteratee, Iteratee._
import effect._, IO._

import scala.concurrent._
import scala.concurrent.duration._
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions

/**
 * @author ross
 */
class EnumeratorSpec extends Specification with NoTimeConversions {
  import scala.concurrent.ExecutionContext.Implicits.global

  "Enumerating a range" should {
    "not crash with Play" in {
      Await.result(Enumerator.enumerate(Stream.from(1)).through(Enumeratee.take(500)).run(play.api.libs.iteratee.Iteratee.fold(0) { _ + _ }), 5 seconds) should_==(500500)
    }

    "not crash with Scalaz" in {
      assert((take[Int, List](500) zip foldM[Int, Id, Int](0) { _ + _ } &= enumStream[Int, Id](Stream.from(1))).run should_==(500500))
    }
  }
}
