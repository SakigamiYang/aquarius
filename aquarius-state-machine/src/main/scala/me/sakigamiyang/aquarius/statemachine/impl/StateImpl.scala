package me.sakigamiyang.aquarius.statemachine.impl

import me.sakigamiyang.aquarius.statemachine.{State, Transition, Visitor}

import scala.collection.concurrent

class StateImpl[S, E, C](final protected val stateId: S) extends State[S, E, C] with Serializable with Equals {
  private[this] val transitions: concurrent.Map[E, Transition[S, E, C]] = concurrent.TrieMap.empty

  override def getId: S = stateId

  override def addTransition(event: E, target: State[S, E, C], transitionType: TransitionType.Value): Transition[S, E, C] = {
    val newTransition = new TransitionImpl[S, E, C](this, target, event, transitionType)
    verify(event, newTransition)
    transitions.update(event, newTransition)
    newTransition
  }

  private[this] def verify(event: E, transition: TransitionImpl[S, E, C]): Unit = {
    transitions.get(event) match {
      case Some(value) if value == transition => throw new StateMachineException(s"$transition already exists")
    }
  }

  override def getTransition(event: E): Option[Transition[S, E, C]] = transitions.get(event)

  override def getTransitions: Iterable[Transition[S, E, C]] = transitions.values

  override def accept(visitor: Visitor): String = visitor.visitOnEntry(this) + visitor.visitOnExit(this)

  override def toString: String = stateId.toString

  override def canEqual(that: Any): Boolean = that.isInstanceOf[StateImpl[S, E, C]]

  override def equals(obj: Any): Boolean =
    obj match {
      case that: StateImpl[S, E, C] =>
        that.canEqual(this) && that.hashCode() == this.hashCode() && that.stateId == this.stateId
      case _ => false
    }

  override def hashCode(): Int = stateId.hashCode()
}
