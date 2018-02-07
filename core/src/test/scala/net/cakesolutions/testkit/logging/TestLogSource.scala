// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging

import scala.concurrent.Promise

import monix.execution.{Cancelable, Scheduler}
import monix.reactive.Observable
import monix.reactive.observers.Subscriber

import net.cakesolutions.utils.ValueDiscard

class TestLogSource[LogLine](testData: Observable[LogLine]) extends LineLoggingSource[LogLine] {

  /** @inheritdoc*/
  override protected def subscriberPolling(
    subscriber: Subscriber[LogLine],
    cancelP: Promise[Unit]
  )(
    implicit scheduler: Scheduler
  ): Unit = {
    ValueDiscard[Cancelable] {
      testData
        .doOnComplete(() => cancelP.success(()))
        .subscribe(subscriber)
    }
  }
}
