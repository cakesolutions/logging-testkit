// Copyright 2018 Cake Solutions Limited
// Copyright 2016-2017 Carl Pulley

package net.cakesolutions.testkit.monitor

/**
  * Notification action type
  */
sealed trait Notify {
  /**
    * Negate the current notification action.
    *
    * @return negated or inverted notification action
    */
  def invert: Notify
}

/**
  * Notification action that indicates a successful or accepting observation.
  *
  * @param failures should this action result from negation, then we store the past failures
  */
final case class Accept(failures: String*) extends Notify {
  override def invert: Notify = {
    Fail(failures: _*)
  }
}

/**
  * Notification action that indicates a failing observation.
  *
  * @param reasons reasons for this failure
  */
final case class Fail(reasons: String*) extends Exception(reasons.mkString(", ")) with Notify {
  override def invert: Notify = {
    Accept(reasons: _*)
  }
}
