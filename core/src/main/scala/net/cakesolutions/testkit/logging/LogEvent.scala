// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging

import java.time.ZonedDateTime

/**
  * Log event that has been parsed and unmarshalled into a type instance.
  *
  * @param time date and time for the logging event
  * @param image identifier for the Docker container from which logging has originated
  * @param message unmarshalled instance of the parsed logging event
  * @tparam A type of the unmarshalled logging instance
  */
final case class LogEvent[A](time: ZonedDateTime, image: String, message: A)
