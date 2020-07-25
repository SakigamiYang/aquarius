package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.logging.Logging

import scala.collection.mutable
import scala.reflect.runtime.universe._

class StateMachine[TState, TTrigger] private[statemachine](private val states: Map[TState, State[TState]],
                                                           private val transmissions: Map[(State[TState], TTrigger), State[TState]],
                                                           private val initialState: State[TState])
  extends Serializable with Logging {

  private[this] var lastState: Option[State[TState]] = None

  private[this] var currentState: State[TState] = initialState

  def getInitialState: State[TState] = initialState

  def getLastState: State[TState] = lastState match {
    case Some(value) => value
    case _ => throw new StateMachineException(s"StateMachine is just initialized, last state is undefined")
  }

  def getCurrentState: State[TState] = currentState

  def fire(trigger: TTrigger,
           currentStateExitArgs: Seq[(Type, Any)],
           nextStateEnterArgs: Seq[(Type, Any)]): StateMachine[TState, TTrigger] = {
    transmissions.get((currentState, trigger)) match {
      case Some(nextState) =>
        lastState match {
          case Some(value) => value.exit(currentStateExitArgs)
          case None =>
        }
        lastState = Some(currentState)
        currentState = nextState
        currentState.enter(nextStateEnterArgs)
      case None => throw new StateMachineException(s"Transmission [$currentState, $trigger] is undefined")
    }
    this
  }

  def forceState(state: TState,
                 currentStateExitArgs: Seq[(Type, Any)],
                 nextStateEnterArgs: Seq[(Type, Any)]): StateMachine[TState, TTrigger] = {
    val ns = states.get(state) match {
      case Some(value) => value
      case None => throw new StateMachineException(s"State [$state] is undefined")
    }
    lastState match {
      case Some(value) => value.exit(currentStateExitArgs)
      case None =>
    }
    lastState = Some(currentState)
    currentState = ns
    currentState.enter(nextStateEnterArgs)
    this
  }

  def toFormattedString(newline: String = "\n", indent: Int = 2): String = {
    require(newline != null)
    require(indent >= 0)
    val indentSpaces = " " * (if (newline.isEmpty) 0 else indent)
    val sb = new mutable.StringBuilder
    sb ++= "StateMachine(" ++= newline
    for (((state, trigger), nextState) <- transmissions) {
      sb ++= indentSpaces
      sb ++= s"$state*$trigger->$nextState,"
      sb ++= newline
    }
    sb.delete(sb.length - 1 - newline.length, sb.length)
    sb ++= newline ++= ")"
    sb.result()
  }

  override def toString: String = toFormattedString(newline = "")
}

object StateMachine extends Logging {

  class Builder[TState, TTrigger] {
    private[this] var initialState: State[TState] = _
    private[this] var states: Map[TState, State[TState]] = Map()
    private[this] var transmissions: Map[(State[TState], TTrigger), State[TState]] = Map()

    def initState(state: TState): Builder[TState, TTrigger] = {
      initialState = states.get(state) match {
        case Some(value) => value
        case None => throw new StateMachineException(s"State [$state] is undefined")
      }

      this
    }

    def addState(state: TState,
                 enterAction: Seq[(Type, Any)] => Unit = (_: Seq[(Type, Any)]) => (),
                 exitAction: Seq[(Type, Any)] => Unit = (_: Seq[(Type, Any)]) => ()): Builder[TState, TTrigger] = {
      if (states.contains(state))
        throw new StateMachineException(s"State [$state] should be added only once")
      states +=
        state -> State.builder()
          .setState(state)
          .registerEnterAction(enterAction)
          .registerExitAction(exitAction)
          .build()
      this
    }

    def addTransmission(state: TState, trigger: TTrigger, nextState: TState): Builder[TState, TTrigger] = {
      require(state != null)
      require(trigger != null)
      require(nextState != null)

      val s = states.get(state) match {
        case Some(value) => value
        case None => throw new StateMachineException(s"State [$state] is undefined")
      }
      val ns = states.get(nextState) match {
        case Some(value) => value
        case None => throw new StateMachineException(s"State [$nextState] is undefined")
      }
      if (transmissions.contains((s, trigger)))
        throw new StateMachineException(s"Transmission [$state $trigger] should be added only once")
      transmissions += (s, trigger) -> ns
      this
    }

    def build(): StateMachine[TState, TTrigger] = {
      new StateMachine(states, transmissions, initialState)
    }
  }

  def builder[TState, TTrigger](): Builder[TState, TTrigger] = new Builder[TState, TTrigger]
}
