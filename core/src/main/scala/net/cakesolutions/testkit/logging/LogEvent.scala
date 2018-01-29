// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging

import java.time.ZonedDateTime

/**
  * TODO:
  *
  * @param time
  * @param image
  * @param message
  * @tparam A
  */
final case class LogEvent[A](time: ZonedDateTime, image: String, message: A)
