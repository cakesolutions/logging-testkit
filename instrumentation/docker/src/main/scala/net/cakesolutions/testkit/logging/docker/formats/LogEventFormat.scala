// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging.docker.formats

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scala.util.control.NonFatal

import cats.syntax.either._
import io.circe._
import net.cakesolutions.testkit.logging.LogEvent

/**
  * Utility methods for transforming between log lines and JSON log events.
  */
object LogEventFormat {
  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
  private val logLineRE = """^([^\s]+)\s+\|\s+([^\s]+)\s+(.*)\z""".r

  case object MatchingFailure extends Exception("MatchingFailure")

  /**
    * Parse a log line into a JSON logging event.
    *
    * @param rawLine log line
    * @return None if the log line is empty, otherwise the result of parsing the log line
    */
  def parse(rawLine: String): Option[Either[ParsingFailure, LogEvent[Json]]] = {
    val line = rawLine.trim

    if (line.nonEmpty) {
      Some(
        try {
          logLineRE.findFirstMatchIn(line) match {
            case Some(logLineMatch) =>
              val id = logLineMatch.group(1).trim
              val time = logLineMatch.group(2).trim
              val message = logLineMatch.group(3).trim
              (for {
                 json <- parser.parse(message)
               } yield LogEvent[Json](ZonedDateTime.parse(time, formatter), id, json)
              ).recover {
                case NonFatal(_) =>
                  LogEvent[Json](ZonedDateTime.parse(time, formatter), id, Json.fromString(message))
              }
            case None =>
              Left(ParsingFailure("Match failure", MatchingFailure))
          }
        } catch {
          case NonFatal(exn) =>
            Left(ParsingFailure("Unexpected exception", exn))
        }
      )
    } else {
      None
    }
  }
}
