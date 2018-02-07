// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker.formats

import java.io.InputStream
import java.time.format.DateTimeFormatter

import scala.io.Source

import io.circe.Json
import net.cakesolutions.testkit.generators.TestGenerators.logEventGen
import net.cakesolutions.testkit.logging.LogEvent
import org.scalatest.{FreeSpec, Inside, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class LogEventFormatTest extends FreeSpec with Matchers with Inside with GeneratorDrivenPropertyChecks {

  val sampleLogStream: InputStream = getClass.getResourceAsStream("/sample-docker-logging.txt")
  val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  "Can serialise and deserialise logging events" in {
    forAll(logEventGen()) { logEvent =>
      inside(LogEventFormat.parse(decodeAsString(logEvent))) {
        case Some(Right(event)) =>
          event.image shouldEqual logEvent.image
          event.message shouldEqual logEvent.message
          event.time.toInstant shouldEqual logEvent.time.toInstant
      }
    }
  }

  "Actual Docker Logging can be parsed" in {
    Source.fromInputStream(sampleLogStream).getLines.foreach { line =>
      inside(LogEventFormat.parse(line)) {
        case Some(Right(_)) =>
          assert(true)
      }
    }
  }

  private def decodeAsString(event: LogEvent[Json]): String = {
    s"${event.image} | ${formatter.format(event.time)} ${event.message.noSpaces}"
  }
}
