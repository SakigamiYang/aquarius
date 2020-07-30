package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.io.{NewLine, NewLines}

import scala.collection.mutable

/**
 * State machine
 *
 * @param states       states (Map from 'state' to State instance)
 * @param transitions  transmissions (Map from 'state * trigger' to Transition instance)
 * @param initialState initial state
 * @tparam TState   type of state
 * @tparam TTrigger type of trigger
 */
final class StateMachine[TState, TTrigger] private[statemachine]
(private val states: Map[TState, State[TState]],
 private val transitions: Map[(TState, TTrigger), Transition[TState, TTrigger]],
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

  /**
   * Fire a trigger to transmit this state machine to next state.
   *
   * @param trigger trigger
   * @param args    arguments of enter action for next state
   * @return state machine
   */
  def fire(trigger: TTrigger, args: Any*): StateMachine[TState, TTrigger] = {
    transitions.get((getCurrentState, trigger)) match {
      case Some(value) =>
        lastState = Some(currentState)
        value.transitEvent(args)
        currentState = value.to
        currentState.ontoEvent()
      case None => throw new StateMachineException(s"Transmission [$currentState*$trigger] is undefined")
    }
    this
  }

  /**
   * Force to set specific state to this state machine.
   *
   * @param state specific state
   * @return state machine
   */
  def forceState(state: TState): StateMachine[TState, TTrigger] = {
    states.get(state) match {
      case Some(value) =>
        lastState = Some(currentState)
        currentState = value
        currentState.ontoEvent()
      case None => throw new StateMachineException(s"State [$state] is undefined")
    }
    this
  }

  /**
   * To formatted string.
   *
   * @param newLine separator for newline
   * @param indent  length of indent spaces
   * @return formatted string
   */
  def toFormattedString(newLine: NewLine = NewLines.SYSTEM_DEPENDENT, indent: Int = 2): String = {
    require(indent >= 0)

    val indentSpaces = " " * indent
    transitions.map {
      case (_, transition) => s"$indentSpaces${transition.from}*${transition.on}->${transition.to}"
    }.mkString(s"StateMachine($newLine", s",$newLine", s"$newLine)")
  }

  /**
   * To string.
   *
   * @return string
   */
  override def toString: String = {
    transitions.map {
      case (_, transition) => s"${transition.from}*${transition.on}->${transition.to}"
    }.mkString("StateMachine(", ",", ")")
  }
}

object StateMachine {

  type TTransitEvent = Seq[Any] => Unit
  type TOntoEvent = () => Unit

  class Builder[TState, TTrigger] {
    private[this] var initialState: TState = _
    private[this] var states: mutable.Buffer[(TState, TOntoEvent)] = mutable.Buffer.empty
    private[this] var transitions: mutable.Buffer[((TState, TTrigger), (TState, TTransitEvent))] = mutable.Buffer.empty

    def init(state: TState): Builder[TState, TTrigger] = {
      initialState = state
      this
    }

    def permit(from: TState, to: TState, on: TTrigger, callee: TTransitEvent = defaultTransitEvent): Builder[TState, TTrigger] = {
      onto(from)
      onto(to)
      transitions :+= ((from, on), (to, callee))
      this
    }

    def onto(state: TState, callee: TOntoEvent = defaultOntoEvent): Builder[TState, TTrigger] = {
      states :+= (state, callee)
      this
    }

    private[this] def validate(): Either[String, Unit] = {
      var errorKeys: String = ""
      val statesLiteral = states.map(_._1).toSet

      // Check whether a state was defined by more than once.
      errorKeys = states.groupBy(_._1)
        .mapValues(_.length)
        .filter(_._2 > 1)
        .keys
        .mkString(",")
      if (!errorKeys.isEmpty)
        return Left(s"States [$errorKeys] should be defined only once")

      // Check whether a tuple of (state, trigger) was defined by more than once.
      errorKeys = transitions.groupBy(_._1)
        .mapValues(_.length)
        .filter(_._2 > 1)
        .keys
        .map { case (state, trigger) => s"$state*$trigger" }
        .mkString(",")
      if (!errorKeys.isEmpty)
        return Left(s"Transmissions [$errorKeys] should be added only once")

      // Check whether all states in transmissions (including state and nextState) are defined in states.
      errorKeys = transitions.flatMap(t => Seq(t._1._1, t._2._1))
        .distinct
        .filterNot(statesLiteral.contains)
        .mkString(",")
      if (!errorKeys.isEmpty)
        return Left(s"States [$errorKeys] in transmissions are undefined")

      // Check whether the initial state is defined in states.
      if (!statesLiteral.contains(initialState))
        return Left(s"Initial state [$initialState] is undefined")

      Right(Unit)
    }

    def build(): StateMachine[TState, TTrigger] = {
      validate() match {

        case Right(_) =>
          // states
          val internalStates = states.map {
            case (state, ontoEvent) => (state, State(state, ontoEvent))
          }.toMap
          // transmissions
          val internalTransitions = transitions.map {
            case ((state, trigger), (nextState, transitEvent)) =>
              ((state, trigger), Transition(internalStates(state), internalStates(nextState), trigger, transitEvent))
          }.toMap
          // initial state
          val internalInitialState = internalStates(initialState)
          // state machine
          new StateMachine(internalStates, internalTransitions, internalInitialState)

        case Left(message) => throw new StateMachineException(message)
      }
    }
  }

  def builder[TState, TTrigger](): Builder[TState, TTrigger] = new Builder[TState, TTrigger]
}
