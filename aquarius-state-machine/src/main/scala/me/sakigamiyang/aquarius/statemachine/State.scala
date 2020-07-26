package me.sakigamiyang.aquarius.statemachine

import scala.reflect.runtime.universe._

/**
 * State class.
 *
 * @param state       content of state
 * @param enterAction function to run when entering in this state
 * @param exitAction  function to run when leaving from this state
 * @tparam TState type of state
 */
final class State[TState] private[statemachine](private var state: TState,
                                                private var enterAction: Seq[(Type, Any)] => Unit,
                                                private var exitAction: () => Unit)
  extends Equals with Serializable {

  def getState: TState = state

  def enter(args: Seq[(Type, Any)]): Unit = enterAction(args)

  def exit(): Unit = exitAction()

  override def canEqual(that: Any): Boolean = that.isInstanceOf[State[TState]]

  override def equals(obj: Any): Boolean = obj match {
    case that: State[TState] =>
      that.canEqual(this) && this.hashCode == that.hashCode && this.getState == that.getState
    case _ => false
  }

  override def hashCode(): Int = getState.hashCode

  override def toString: String = s"State($getState)"
}

object State {

  class Builder[TState] {
    private[this] var internalState: TState = _
    private[this] var enterAction: Seq[(Type, Any)] => Unit = (_: Seq[(Type, Any)]) => ()
    private[this] var exitAction: () => Unit = () => ()

    def setState(state: TState): Builder[TState] = {
      internalState = state
      this
    }

    def registerEnterAction(fn: Seq[(Type, Any)] => Unit): Builder[TState] = {
      enterAction = fn
      this
    }

    def registerExitAction(fn: () => Unit): Builder[TState] = {
      exitAction = fn
      this
    }

    def build(): State[TState] = {
      new State(internalState, enterAction, exitAction)
    }
  }

  def builder[TState](): Builder[TState] = new Builder[TState]
}
