package me.sakigamiyang.aquarius.statemachine

import scala.collection.concurrent

/**
 * State machine.
 *
 * @param machineId     machine id
 * @param initState     initial state
 * @param stateMap      state mapping from state id to state
 * @param transitionMap transition mapping from (state, event) to transition
 * @tparam S type of state
 * @tparam E type of event
 * @tparam C type of context
 */
class StateMachine[S, E, C] private[StateMachine]
(private[this] final val machineId: String,
 private[this] final val initState: State[S, E, C],
 private[this] final val stateMap: concurrent.Map[S, State[S, E, C]],
 private[this] final val transitionMap: concurrent.Map[(State[S, E, C], E), Transition[S, E, C]])
  extends Serializable {

  private[this] var currentState: State[S, E, C] = initState

  /**
   * Get machine id.
   *
   * @return machine id
   */
  def getMachineId: String = machineId

  /**
   * Get state id of current state.
   *
   * @return current state id
   */
  def getCurrentState: S = currentState.getId

  /**
   * Fire an event.
   *
   * @param event   event
   * @param context context for condition and action of event
   * @return state machine
   */
  def fire(event: E, context: C): StateMachine[S, E, C] = {
    currentState = transitionMap.get((currentState, event)) match {
      case Some(transition) => transition.transit(context)
      case None => throw new StateMachineException(s"Event [$event] for state [$currentState] is undefined")
    }
    this
  }

  /**
   * Force to set any state without trigger any action.
   *
   * @param stateId state id
   * @return state machine
   */
  def force(stateId: S): StateMachine[S, E, C] = {
    currentState = stateMap.get(stateId) match {
      case Some(state) => state
      case None => throw new StateMachineException(s"State [$stateId] is undefined")
    }
    this
  }
}

/**
 * State machine.
 */
object StateMachine {

  class Builder[S, E, C] {
    private[this] var machineId: String = ""
    private[this] var initState: Option[State[S, E, C]] = None
    private[this] val stateMap: concurrent.Map[S, State[S, E, C]] = concurrent.TrieMap.empty
    private[this] val transitionMap: concurrent.Map[(State[S, E, C], E), Transition[S, E, C]] = concurrent.TrieMap.empty

    /**
     * Set machine id.
     *
     * @param machineId machine id
     * @return StateMachine.Builder
     */
    def setMachineId(machineId: String): Builder[S, E, C] = {
      this.machineId = machineId
      this
    }

    /**
     * Initialize state.
     *
     * @param stateId state id
     * @return StateMachine.Builder
     */
    def init(stateId: S): Builder[S, E, C] = {
      initState = Some(stateMap.getOrElseUpdate(stateId, new State(stateId)))
      this
    }

    /**
     * Permit a transition.
     *
     * @param from    source state id
     * @param to      target state id
     * @param on      event
     * @param when    condition
     * @param perform action
     * @return StateMachine.Builder
     */
    def permit(from: S,
               to: S,
               on: E,
               when: Condition.Func[C] = Condition.noCondition,
               perform: Action.Func[S, E, C] = Action.noAction): Builder[S, E, C] = {
      val sourceState = stateMap.getOrElseUpdate(from, new State(from))
      val targetState = stateMap.getOrElseUpdate(to, new State(to))
      transitionMap.get((sourceState, on)) match {
        case Some(_) => throw new StateMachineException(s"Event [$on] for state [$from] is redefined")
        case None => transitionMap.update((sourceState, on), new Transition(sourceState, targetState, on, when, perform))
      }
      this
    }

    /**
     * Build the state machine.
     *
     * @return state machine
     */
    def build(): StateMachine[S, E, C] = {
      if (machineId.trim.isEmpty)
        throw new StateMachineException("The id of state machine should be set up")

      if (initState.isEmpty)
        throw new StateMachineException(s"The initial state of state machine [$machineId] is undefined")

      val stateMachine = new StateMachine(machineId, initState.get, stateMap, transitionMap)
      StateMachineFactory.register(stateMachine)
    }
  }

  /**
   * Get a state machine builder.
   *
   * @tparam S type of state
   * @tparam E type of event
   * @tparam C type of context
   * @return StateMachine.Builder
   */
  def builder[S, E, C]() = new Builder
}
