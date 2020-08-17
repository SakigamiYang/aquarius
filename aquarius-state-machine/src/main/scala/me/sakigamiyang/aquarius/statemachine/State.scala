package me.sakigamiyang.aquarius.statemachine

/**
 * State.
 *
 * @param stateId state id
 * @tparam S type of state
 * @tparam E type of event
 * @tparam C type of context
 */
class State[S, E, C] private[statemachine](private final val stateId: S)
  extends Serializable with Equals {

  /**
   * Get state id.
   *
   * @return state id
   */
  def getId: S = stateId

  override def canEqual(that: Any): Boolean = that.isInstanceOf[State[S, E, C]]

  override def equals(obj: Any): Boolean =
    obj match {
      case that: State[S, E, C] =>
        that.canEqual(this) &&
          that.hashCode() == this.hashCode() &&
          that.stateId == this.stateId
      case _ => false
    }

  override def hashCode(): Int = stateId.hashCode()

  override def toString: String = stateId.toString
}
