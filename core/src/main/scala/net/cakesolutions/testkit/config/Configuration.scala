// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.config

import scala.concurrent.duration._

import net.cakesolutions.testkit.config.Environment.Optional

/**
  * Library configuration.
  */
object Configuration {
  object Logging {
    /**
      * Logger name.
      */
    val name: String = "LoggingTestkit"
  }

  object Timeout {
    /**
      * @see net.cakesolutions.testkit.config.Environment.Optional.TIMEOUT_DELAY
      */
    val delay: FiniteDuration = Optional.TIMEOUT_DELAY.withDefault(0.seconds)

    /**
      * @see net.cakesolutions.testkit.config.Environment.Optional.TIMEOUT_PERIOD
      */
    val period: FiniteDuration = Optional.TIMEOUT_PERIOD.withDefault(100.milliseconds)
  }

  private implicit class ConfigurationHelper(value: Option[String]) {
    def withDefault(default: FiniteDuration): FiniteDuration = {
      value.map(Duration(_)).collect { case d: FiniteDuration => d }.getOrElse(default)
    }
  }
}
