package me.sakigamiyang.aquarius.statemachine

/**
 * Condition.
 * Do check while transiting from a state to another state.
 */
object Condition {
  type Func[C] = C => Boolean

  /**
   * Default condition (do not do any check).
   *
   * @tparam C type of context
   * @return Condition.Func
   */
  def noCondition[C]: Func[C] = (_: C) => true
}
