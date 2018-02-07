// Copyright 2018 Cake Solutions Limited
// Copyright 2016-2017 Carl Pulley

package net.cakesolutions.testkit.monitor

object Interactions {

  /**
    * Input events that IOAutomata monitors will consume.
    *
    * @tparam Event type of event that we are to observe
    */
  sealed trait EventIn[+Event]

  /**
    * Internal events.
    *
    * @tparam Event type of event that we are to observe
    */
  sealed trait EventInternal[+Event]

  /**
    * Output actions.
    *
    * @tparam Event type of event that we are to observe
    */
  sealed trait ActionOut[+Event]

  /**
    * Observed an event flow.
    *
    * @param event event that we observed
    * @tparam Event type of event that we are to observe
    */
  final case class Observe[Event](event: Event) extends EventIn[Event] with EventInternal[Event] with ActionOut[Event]

  private[monitor] case object Tick extends EventInternal[Nothing]

  /**
    * The IOAutomata monitor timed out whilst waiting in some state.
    */
  case object StateTimeout extends Exception("StateTimeout") with EventIn[Nothing] with EventInternal[Nothing] with ActionOut[Nothing]

  /**
    * The IOAutomata monitor has exceeded its overall timeout.
    */
  case object MonitorTimeout extends Exception("MonitorTimeout") with EventIn[Nothing] with EventInternal[Nothing] with ActionOut[Nothing]

  /**
    * Error signal indicating that the IOAutomata was not defined for a given event.
    *
    * @param event event that the IOAutomata was not defined for
    * @tparam Event type of event that we are to observe
    */
  final case class TransitionFailure[Event](event: Event) extends Exception(s"TransitionFailure($event)") with ActionOut[Nothing]

  /**
    * Error signal wrapping uncaught exceptions.
    *
    * @param exn throwable that was caught
    */
  final case class UnexpectedException(exn: Throwable) extends Exception(s"UnexpectedException($exn)") with ActionOut[Nothing]
}
