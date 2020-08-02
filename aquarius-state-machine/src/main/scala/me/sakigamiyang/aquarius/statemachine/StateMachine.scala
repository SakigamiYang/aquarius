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

  private[this] def transitTo(from: State[TState], to: State[TState]): Unit = {
    lastState = Some(from)
    currentState = to
    currentState.onto()
  }

  /**
   * Fire a trigger to transmit this state machine to next state.
   *
   * @param trigger trigger
   * @param args    arguments of enter action for next state
   * @return state machine
   */
  def fire(trigger: TTrigger, args: Any*): StateMachine[TState, TTrigger] = {
    transitions.get((getCurrentState, trigger)) match {
      case Some(transition) =>
        transition.transitEvent(args)
        transitTo(currentState, transition.to)
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
      case Some(state) =>
        transitTo(currentState, state)
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
    private[this] var states: mutable.Map[TState, TOntoEvent] = mutable.Map.empty
    private[this] var transitions: mutable.Map[(TState, TTrigger), (TState, TTransitEvent)] = mutable.Map.empty
    private[this] var reachableStates: mutable.Set[TState] = mutable.Set.empty

    def init(state: TState): Builder[TState, TTrigger] = {
      initialState = state
      this
    }

    def permit(from: TState, to: TState, on: TTrigger, callee: TTransitEvent = defaultTransitEvent): Builder[TState, TTrigger] = {
      onto(from)
      onto(to)
      transitions += (from, on) -> (to, callee)
      this
    }

    def onto(state: TState, callee: TOntoEvent = defaultOntoEvent): Builder[TState, TTrigger] = {
      states += state -> callee
      this
    }

    private[this] def allReachableFromState(state: TState): Boolean = {
      // Add current state into reachable states.
      reachableStates += state
      // Check all states which can be transited to from current state.
      transitions.filterKeys { case (from, _) => from == state }
        .values
        .foreach { case (to, _) =>
          if (!reachableStates.contains(to)) {
            // Only check the states that we haven't known whether it was reachable.
            allReachableFromState(to)
          }
        }
      states.keySet.diff(reachableStates).isEmpty
    }

    private[this] def validate(): Either[String, Unit] = {
      // Check whether the initial state is defined in states.
      if (!states.contains(initialState))
        return Left(s"Initial state [State($initialState)] is undefined")

      // Check whether there is an unreachable state.
      if (!allReachableFromState(initialState)) {
        val unreachableStates = states.keys.filterNot(reachableStates.contains)
        return Left(s"States [${unreachableStates.map(s => s"State($s)").mkString(",")}] are unreachable from initial state")
      }

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
