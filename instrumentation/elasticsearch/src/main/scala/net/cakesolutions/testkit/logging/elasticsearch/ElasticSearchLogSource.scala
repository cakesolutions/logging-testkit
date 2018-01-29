// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.elasticsearch

import scala.concurrent.Promise

import io.circe.Json
import monix.execution.Scheduler
import monix.reactive.observers.Subscriber

import net.cakesolutions.testkit.logging.{LogEvent, LoggingSource}

// $COVERAGE-OFF$ disabled until this is implemented

object ElasticSearchLogSource extends LoggingSource[Json] {

  /** @see net.cakesolutions.testkit.logging.LoggingSource */
  override protected def subscriberPolling(
    id: String,
    subscriber: Subscriber[LogEvent[Json]],
    cancelP: Promise[Unit]
  )(implicit
    scheduler: Scheduler
  ): Unit = {
    ??? // TODO: needs implementing
  }
}

// $COVERAGE-ON$
