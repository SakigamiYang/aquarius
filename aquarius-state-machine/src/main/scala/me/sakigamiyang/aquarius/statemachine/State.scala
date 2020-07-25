package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.logging.Logging

import scala.reflect.runtime.universe._

class State[TState] private[statemachine](private var state: TState,
                                          private var enterAction: Seq[(Type, Any)] => Unit,
                                          private var exitAction: Seq[(Type, Any)] => Unit)
  extends Equals with Serializable with Logging {

  def getState: TState = state

  def enter(args: Seq[(Type, Any)]): Unit = enterAction(args)

  def exit(args: Seq[(Type, Any)]): Unit = exitAction(args)

  override def canEqual(that: Any): Boolean = that.isInstanceOf[State[TState]]

  override def equals(obj: Any): Boolean = obj match {
    case that: State[TState] =>
      that.canEqual(this) && this.hashCode == that.hashCode && this.getState == that.getState
    case _ => false
  }

  override def hashCode(): Int = getState.hashCode

  override def toString: String = getState.toString
}

object State extends Logging {

  class Builder[TState] {
    private[this] var internalState: TState = _
    private[this] var enterAction: Seq[(Type, Any)] => Unit = (_: Seq[(Type, Any)]) => ()
    private[this] var exitAction: Seq[(Type, Any)] => Unit = (_: Seq[(Type, Any)]) => ()

    def setState(state: TState): Builder[TState] = {
      internalState = state
      this
    }

    def registerEnterAction(fn: Seq[(Type, Any)] => Unit): Builder[TState] = {
      enterAction = fn
      this
    }

    def registerExitAction(fn: Seq[(Type, Any)] => Unit): Builder[TState] = {
      exitAction = fn
      this
    }

    def build(): State[TState] = {
      new State(internalState, enterAction, exitAction)
    }
  }

  def builder[TState](): Builder[TState] = new Builder[TState]
}
