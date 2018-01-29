// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.config

import scala.concurrent.duration._

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
      * Duration that we wait for before allowing monitor timeout's to execute.
      */
    val delay: FiniteDuration = sys.env.get("TIMEOUT_DELAY").withDefault(0.seconds)
    /**
      * Periodicity with which monitor timeouts are checked for expiration.
      */
    val period: FiniteDuration = sys.env.get("TIMEOUT_PERIOD").withDefault(100.milliseconds)
  }

  private implicit class ConfigurationHelper(value: Option[String]) {
    def withDefault(default: FiniteDuration): FiniteDuration = {
      value.map(Duration(_)).collect { case d: FiniteDuration => d }.getOrElse(default)
    }
  }
}
