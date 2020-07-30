package me.sakigamiyang.aquarius.statemachine

/**
 * State class.
 *
 * @param state     content of state
 * @param ontoEvent event to call when onto this state
 * @tparam TState type of state
 */
case class State[TState](state: TState, ontoEvent: TOntoEvent)
  extends Equals with Serializable {

  def getState: TState = state

  def onto(): Unit = ontoEvent()

  override def canEqual(that: Any): Boolean = that.isInstanceOf[State[TState]]

  override def equals(obj: Any): Boolean = obj match {
    case that: State[TState] =>
      that.canEqual(this) && this.hashCode == that.hashCode && this.getState == that.getState
    case _ => false
  }

  override def hashCode(): Int = getState.hashCode

  override def toString: String = s"State($getState)"
}
