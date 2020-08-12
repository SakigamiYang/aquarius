package me.sakigamiyang.aquarius.statemachine

/**
 * Action.
 * Action.Func will be call when transiting from a state to another state after check Condition.
 */
object Action {
  type Func[S, E, C] = (S, S, E, C) => Unit

  /**
   * Default action (do nothing).
   *
   * @tparam S type of state
   * @tparam E type of event
   * @tparam C type of context
   * @return Action.Func
   */
  def noAction[S, E, C]: Func[S, E, C] = (_: S, _: S, _: E, _: C) => ()
}
