// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging

import scala.concurrent.Promise
import scala.util.control.NonFatal

import com.typesafe.scalalogging.Logger
import io.circe.Json
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.{Observable, OverflowStrategy}
import monix.reactive.observers.Subscriber

/**
  * Interface for defining logging sources.
  *
  * @tparam A type of the unmarshalled logging instance
  */
trait LoggingSource[A] {

  private val log = Logger("LoggingTestkit")

  /**
    * Primary interface method that specific logging sources will need to implement.
    *
    * @param id identifies the Docker container from which log events are consumed
    * @param subscriber subscriber that is to receive logging events
    * @param cancelP promise used to communicate that the subscriber has cancelled their subscription
    * @param scheduler (implicit) scheduler that the subscriber uses for running polling events on
    */
  protected def subscriberPolling(id: String, subscriber: Subscriber[LogEvent[Json]], cancelP: Promise[Unit])(implicit scheduler: Scheduler): Unit

  /**
    * For a given Docker container, wrap container logging as an observable.
    *
    * @param id identifies the Docker container from which log events are consumed
    * @param scheduler (implicit) scheduler that the subscriber uses for running polling events on
    * @return observable of JSON logging events
    */
  final def source(id: String)(implicit scheduler: Scheduler): Observable[LogEvent[Json]] =
    Observable.create[LogEvent[Json]](OverflowStrategy.Unbounded) { subscriber =>
      val cancelP = Promise[Unit]

      try {
        scheduler.execute(new Runnable {
          def run(): Unit = {
            subscriberPolling(id, subscriber, cancelP)
          }
        })
      } catch {
        case NonFatal(exn) =>
          log.error("Log parsing error", exn)
          if (! cancelP.isCompleted) {
            cancelP.failure(exn)
            subscriber.onError(exn)
          }
      }

      new Cancelable {
        override def cancel(): Unit = {
          if (! cancelP.isCompleted) {
            cancelP.success(())
            subscriber.onComplete()
          }
        }
      }
    }
}
