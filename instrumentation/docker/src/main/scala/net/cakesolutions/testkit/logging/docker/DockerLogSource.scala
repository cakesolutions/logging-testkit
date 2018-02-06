// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker

import io.circe.Json
import monix.execution.Scheduler
import monix.reactive.Observable
import net.cakesolutions.testkit.logging.LogEvent
import net.cakesolutions.testkit.logging.docker.formats.LogEventFormat

object DockerLogSource {
  /**
    * For project Docker containers, wrap their logging as an observable.
    *
    * @param scheduler (implicit) scheduler that the subscriber uses for running
    *   polling events on
    * @return observable of observed logging events
    */
  def source()(implicit scheduler: Scheduler): Observable[LogEvent[Json]] = {
    DockerLogLineSource
      .source()
      .flatMap { line =>
        LogEventFormat.parse(line) match {
          case Some(Right(logEntry)) =>
            Observable.pure(logEntry)
          case Some(Left(exn)) =>
            Observable.raiseError(exn)
          case None =>
            Observable.empty
        }
      }
  }
}
