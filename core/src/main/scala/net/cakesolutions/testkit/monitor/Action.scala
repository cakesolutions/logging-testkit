// Copyright 2018 Cake Solutions Limited
// Copyright 2016-2017 Carl Pulley

package net.cakesolutions.testkit.monitor

import scala.concurrent.duration.FiniteDuration

/**
  * Type of action that an IOAutomata may perform on each of its transitions.
  *
  * @tparam IOState IOAutomata state type
  */
sealed trait Action[IOState] {
  /**
    * Notification event that is to be emitted.
    *
    * @return (optional) notification event that is to be emitted
    */
  def emit: Option[Notify]
}

/**
  * Used should the IOAutomata need to transition to another state.
  *
  * @param state state IOAutomata will transition to
  * @param forMax (optional) time we can stay in the next state for
  * @param emit (optional) action that will be emitted should we transition to the next state
  * @tparam IOState IOAutomata state type
  */
final case class Goto[IOState](state: IOState, forMax: Option[FiniteDuration] = None, emit: Option[Notify] = None) extends Action[IOState]
object Goto {
  def apply[IOState](state: IOState, forMax: FiniteDuration): Goto[IOState] = {
    Goto(state, Some(forMax), None)
  }

  def apply[IOState](state: IOState, emit: Notify): Goto[IOState] = {
    Goto(state, None, Some(emit))
  }

  def apply[IOState](state: IOState, forMax: FiniteDuration, emit: Notify): Goto[IOState] = {
    Goto(state, Some(forMax), Some(emit))
  }
}

/**
  * Used should the IOAutomata need to remain or stay in the current state.
  *
  * @param emit (optional) action that will be emitted should we stay in the current state
  * @tparam IOState IOAutomata state type
  */
final case class Stay[IOState](emit: Option[Notify] = None) extends Action[IOState]
object Stay {
  def apply[IOState](emit: Notify): Stay[IOState] = {
    Stay(Some(emit))
  }
}

/**
  * Used should the IOAutomata need to terminate or stop in the current state. The success or failure
  * of this termination is determined by the notification action that is emitted.
  *
  * @param toEmit action that will be emitted should we terminate or stop
  * @tparam IOState IOAutomata state type
  */
final case class Stop[IOState](toEmit: Notify) extends Action[IOState] {
  override val emit = Some(toEmit)
}
