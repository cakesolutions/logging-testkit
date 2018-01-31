// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging.docker.formats

import java.time.{ZonedDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

import scala.util.Try
import scala.util.control.NonFatal

import cats.syntax.either._
import com.typesafe.scalalogging.Logger
import io.circe._

import net.cakesolutions.testkit.config.Configuration.Logging
import net.cakesolutions.testkit.logging.LogEvent

/**
  * Utility methods for transforming between log lines and JSON log events.
  *
  * @param id identifies source of log lines
  */
class LogEventFormat(id: String) {
  private val log = Logger(Logging.name)
  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
  private val logLineRE = """^([^\s]+)\s+(.*)\z""".r

  /**
    * Parse a log line into a JSON logging event.
    *
    * @param rawLine log line
    * @return None if the log line is empty, otherwise the result of parsing the log line
    */
  def parse(rawLine: String): Option[Either[ParsingFailure, LogEvent[Json]]] = {
    val line = rawLine.trim
    log.debug(s"$id $line")

    if (line.nonEmpty) {
      Some(
        try {
          logLineRE.findFirstMatchIn(line) match {
            case Some(logLineMatch) =>
              val time = logLineMatch.group(1)
              val message = logLineMatch.group(2).trim
              (for {
                 json <- parser.parse(message)
               } yield LogEvent[Json](ZonedDateTime.parse(time, formatter), id, json)
              ).recover {
                case NonFatal(_) =>
                  LogEvent[Json](ZonedDateTime.parse(time, formatter), id, Json.fromString(message))
              }
            case None =>
              (for {
                 json <- parser.parse(line)
               } yield LogEvent[Json](ZonedDateTime.now(ZoneOffset.UTC), id, json)
              ).recover {
                case NonFatal(_) =>
                  LogEvent[Json](ZonedDateTime.now(ZoneOffset.UTC), id, Json.fromString(line))
              }
          }
        } catch {
          case NonFatal(exn) =>
            exn.printStackTrace()
            Left(ParsingFailure("Unexpected exception", exn))
        }
      )
    } else {
      None
    }
  }

  /**
    * Converts a JSON log event to a log line.
    *
    * @param logEvent JSON log event
    * @return log line if successful, otherwise the failure cause
    */
  def asString(logEvent: LogEvent[Json]): Try[String] = Try {
    val datetime = formatter.format(logEvent.time)
    val message = logEvent.message.noSpaces

    s"$datetime $message"
  }
}
