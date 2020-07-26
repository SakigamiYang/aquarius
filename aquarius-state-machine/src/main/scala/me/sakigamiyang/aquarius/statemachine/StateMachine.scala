package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.io.FileUtils

import scala.collection.mutable
import scala.reflect.runtime.universe._

/**
 * State machine
 *
 * @param states        states
 * @param transmissions transmissions (Map from 'state * trigger' to 'nextState')
 * @param initialState  initial state
 * @tparam TState   type of state
 * @tparam TTrigger type of trigger
 */
final class StateMachine[TState, TTrigger] private[statemachine](private val states: Map[TState, State[TState]],
                                                                 private val transmissions: Map[(State[TState], TTrigger), State[TState]],
                                                                 private val initialState: State[TState])
  extends Serializable {

  private[this] var lastState: Option[State[TState]] = None

  private[this] var currentState: State[TState] = initialState

  /**
   * Get initial state.
   *
   * @return initial state
   */
  def getInitialState: TState = initialState.getState

  /**
   * Get last state.
   *
   * @return last state
   */
  def getLastState: TState = lastState match {
    case Some(value) => value.getState
    case _ => throw new StateMachineException(s"StateMachine is just initialized without having last state")
  }

  /**
   * Get current state.
   *
   * @return current state
   */
  def getCurrentState: TState = currentState.getState

  private[this] def transmitTo(nextState: Option[State[TState]],
                               errorMessage: String,
                               args: Seq[(Type, Any)]): Unit = {
    nextState match {
      case Some(value) =>
        currentState.exit()
        lastState = Some(currentState)
        currentState = value
        currentState.enter(args)
      case None => throw new StateMachineException(errorMessage)
    }

  }

  /**
   * Fire a trigger to transmit this state machine to next state.
   *
   * @param trigger trigger
   * @param args    arguments of enter action for next state
   * @return state machine
   */
  def fire(trigger: TTrigger, args: (Type, Any)*): StateMachine[TState, TTrigger] = {
    transmitTo(
      transmissions.get((currentState, trigger)),
      s"Transmission [$currentState*$trigger] is undefined",
      args
    )
    this
  }

  /**
   * Force to set specific state to this state machine.
   *
   * @param state specific state
   * @param args  arguments of enter action for next state
   * @return state machine
   */
  def forceState(state: TState, args: (Type, Any)*): StateMachine[TState, TTrigger] = {
    transmitTo(
      states.get(state),
      s"State [$state] is undefined",
      args)
    this
  }

  /**
   * To formatted string.
   *
   * @param newLine separator for newline
   * @param indent  length of indent spaces
   * @return formatted string
   */
  def toFormattedString(newLine: FileUtils.NewLines = FileUtils.NewLines.SYSTEM_DEPENDENT, indent: Int = 2): String = {
    require(indent >= 0)

    val indentSpaces = " " * indent
    transmissions.map {
      case ((state, trigger), nextState) => s"$indentSpaces$state*$trigger->$nextState"
    }.mkString(s"StateMachine(${newLine.getContent}", s",${newLine.getContent}", s"${newLine.getContent})")
  }

  /**
   * To string.
   *
   * @return string
   */
  override def toString: String = {
    transmissions.map {
      case ((state, trigger), nextState) => s"$state*$trigger->$nextState"
    }.mkString("StateMachine(", ",", ")")
  }
}

object StateMachine {

  type EnterActionType = Seq[(Type, Any)] => Unit
  type ExitActionType = () => Unit

  class Builder[TState, TTrigger] {
    private[this] var initialState: TState = _
    private[this] var states: mutable.Buffer[TState] = mutable.Buffer()
    private[this] var transmissions: mutable.Buffer[(TState, TTrigger, TState)] = mutable.Buffer()
    private[this] var mapStateActions: mutable.Map[TState, (EnterActionType, ExitActionType)] = mutable.Map()

    def initState(state: TState): Builder[TState, TTrigger] = {
      initialState = state
      this
    }

    def addState(state: TState,
                 enterAction: EnterActionType = (_: Seq[(Type, Any)]) => (),
                 exitAction: ExitActionType = () => ()): Builder[TState, TTrigger] = {
      states :+= state
      mapStateActions += state -> (enterAction, exitAction)
      this
    }

    def addTransmission(state: TState, trigger: TTrigger, nextState: TState): Builder[TState, TTrigger] = {
      transmissions :+= (state, trigger, nextState)
      this
    }

    private[this] def validate(): Either[String, Unit] = {
      var errorKeys: String = ""

      // Check whether a state was defined by more than once.
      errorKeys = states.groupBy(identity).mapValues(_.length).filter(_._2 > 1).keys.mkString(",")
      if (!errorKeys.isEmpty) return Left(s"States [$errorKeys] should be defined only once")

      // Check whether a tuple of (state, trigger) was defined by more than once.
      errorKeys = transmissions.groupBy(t => (t._1, t._2))
        .mapValues(_.length)
        .filter(_._2 > 1)
        .keys
        .map { case (state, trigger) => s"$state*$trigger" }
        .mkString(",")
      if (!errorKeys.isEmpty) return Left(s"Transmissions [$errorKeys] should be added only once")

      // Check whether all states in transmissions (including state and nextState) are defined in states.
      errorKeys = transmissions.flatMap(t => Seq(t._1, t._3))
        .distinct
        .filterNot(states.contains)
        .mkString(",")
      if (!errorKeys.isEmpty) return Left(s"States [$errorKeys] in transmissions are undefined")

      // Check whether the initial state is defined in states.
      if (!states.contains(initialState)) return Left(s"Initial state [$initialState] is undefined")

      Right(Unit)
    }

    def build(): StateMachine[TState, TTrigger] = {
      validate() match {

        case Right(_) =>
          // states
          val internalStates = mapStateActions.map {
            case (state, (enterAction, exitAction)) =>
              (state,
                State.builder()
                  .setState(state)
                  .registerEnterAction(enterAction)
                  .registerExitAction(exitAction)
                  .build())
          }.toMap
          // transmissions
          val internalTransmissions = transmissions.map {
            case (state, trigger, nextState) =>
              ((internalStates(state), trigger), internalStates(nextState))
          }.toMap
          // initial state
          val internalInitialState = internalStates(initialState)
          // state machine
          new StateMachine(internalStates, internalTransmissions, internalInitialState)

        case Left(message) => throw new StateMachineException(message)
      }
    }
  }

  def builder[TState, TTrigger](): Builder[TState, TTrigger] = new Builder[TState, TTrigger]
}
