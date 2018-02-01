// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.monitor

import scala.concurrent.duration.FiniteDuration

/**
  * Models an IO automata.
  *
  * @param initialState state that monitoring IOAutomata will start in
  * @param transition partial function describing the monitoring behaviour as an IOAutomata
  * @param initialTimeout (optional) timeout for how long we may linger in the starting state
  * @param overallTimeout (optional) timeout for how long this IOAutomata will monitor
  * @tparam IOState IOAutomata state type
  * @tparam Event event type
  */
final case class IOAutomata[IOState, Event](
  initialState: IOState,
  transition: Behaviour[IOState, Event],
  initialTimeout: Option[FiniteDuration] = None,
  overallTimeout: Option[FiniteDuration] = None
)
