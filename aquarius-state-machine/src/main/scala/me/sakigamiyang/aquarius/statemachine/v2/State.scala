package me.sakigamiyang.aquarius.statemachine.v2

import scala.collection.concurrent

class State[S, E, C] private[State](private final val stateId: S)
  extends Serializable with Equals {

  private[this] final val transitions: concurrent.Map[E, Transition[S, E, C]] = concurrent.TrieMap.empty

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

object State {

  class Builder[S, E, C] {
    private[this] var stateId: S = _

    def setStateId(stateId: S): Builder[S, E, C] = {
      this.stateId = stateId
      this
    }

    def build(): State[S, E, C] = new State(stateId)
  }

  def builder[S, E, C]() = new Builder[S, E, C]
}
