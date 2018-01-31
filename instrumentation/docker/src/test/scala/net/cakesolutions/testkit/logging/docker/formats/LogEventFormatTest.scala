// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker.formats

import java.io.InputStream

import scala.io.Source
import scala.util.Success

import org.scalatest.{FreeSpec, Inside, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import net.cakesolutions.testkit.generators.TestGenerators.logEventGen

class LogEventFormatTest extends FreeSpec with Matchers with Inside with GeneratorDrivenPropertyChecks {

  val sampleLogStream: InputStream = getClass.getResourceAsStream("/sample-docker-logging.txt")
  val decoder: LogEventFormat = new LogEventFormat("test")

  "Can serialise and deserialise logging events" in {
    forAll(logEventGen()) { logEvent =>
      inside(decoder.asString(logEvent).map(decoder.parse)) {
        case Success(Some(Right(event))) =>
          event.image shouldEqual logEvent.image
          event.message shouldEqual logEvent.message
          event.time.toInstant shouldEqual logEvent.time.toInstant
      }
    }
  }

  "Actual Docker Logging can be parsed" in {
    Source.fromInputStream(sampleLogStream).getLines.foreach { line =>
      inside(decoder.parse(line)) {
        case Some(Right(event)) =>
          inside(decoder.asString(event)) {
            case Success(_) =>
              assert(true)
          }
      }
    }
  }
}
