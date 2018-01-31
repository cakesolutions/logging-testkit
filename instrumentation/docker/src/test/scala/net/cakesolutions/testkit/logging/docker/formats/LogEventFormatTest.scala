// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker.formats

import scala.util.Success

import net.cakesolutions.testkit.generators.TestGenerators.logEventGen
import org.scalatest.{FreeSpec, Inside, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class LogEventFormatTest extends FreeSpec with Matchers with Inside with GeneratorDrivenPropertyChecks {

  val decoder = new LogEventFormat("test")

  "Can serialise and deserialise logging events" in {
    forAll(logEventGen()) { logEvent =>
      inside (decoder.asString(logEvent).map(decoder.parse)) {
        case Success(Some(Right(event))) =>
          event.image shouldEqual logEvent.image
          event.message shouldEqual logEvent.message
          event.time.toInstant shouldEqual logEvent.time.toInstant
      }
    }
  }
}
