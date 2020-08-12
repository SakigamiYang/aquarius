package me.sakigamiyang.aquarius.statemachine

class State[S, E, C] private[statemachine](private final val stateId: S)
  extends Serializable with Equals {

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
