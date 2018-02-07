// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging

import scala.concurrent.Promise
import scala.util.control.NonFatal

import com.typesafe.scalalogging.Logger
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.{Observable, OverflowStrategy}
import monix.reactive.observers.Subscriber

/**
  * Interface for defining logging sources.
  *
  * @tparam LogLine type of logging event representing a single log line
  */
trait LineLoggingSource[LogLine] {

  private val log = Logger("LoggingTestkit")

  /**
    * Primary interface method that specific logging sources will need to implement.
    *
    * @param subscriber subscriber that is to receive logging events
    * @param cancelP promise used to communicate that the subscriber has cancelled their subscription
    * @param scheduler (implicit) scheduler that the subscriber uses for running polling events on
    */
  protected def subscriberPolling(
    subscriber: Subscriber[LogLine],
    cancelP: Promise[Unit]
  )(
    implicit scheduler: Scheduler
  ): Unit

  /**
    * For project Docker containers, wrap their logging as an observable.
    *
    * @param scheduler (implicit) scheduler that the subscriber uses for running polling events on
    * @return observable of observed logging lines
    */
  final def source()(implicit scheduler: Scheduler): Observable[LogLine] =
    Observable.create[LogLine](OverflowStrategy.Unbounded) { subscriber =>
      val cancelP = Promise[Unit]

      try {
        scheduler.execute(new Runnable {
          def run(): Unit = {
            subscriberPolling(subscriber, cancelP)
          }
        })
      } catch {
        case NonFatal(exn) =>
          log.error("Unexpected exception", exn)
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
