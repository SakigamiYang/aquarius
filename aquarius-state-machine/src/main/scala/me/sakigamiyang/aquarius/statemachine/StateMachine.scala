package me.sakigamiyang.aquarius.statemachine

import scala.collection.concurrent

class StateMachine[S, E, C] private[StateMachine]
(private[this] final val machineId: String,
 private[this] final val initState: State[S, E, C],
 private[this] final val stateMap: concurrent.Map[S, State[S, E, C]],
 private[this] final val transitionMap: concurrent.Map[(State[S, E, C], E), Transition[S, E, C]])
  extends Serializable {

  private[this] var currentState: State[S, E, C] = initState

  def getMachineId: String = machineId

  def getCurrentState: State[S, E, C] = currentState

  def fire(event: E, context: C): StateMachine[S, E, C] = {
    currentState = transitionMap.get((currentState, event)) match {
      case Some(transition) => transition.transit(context)
      case None => throw new StateMachineException(s"Event [$event] for state [$currentState] is undefined")
    }
    this
  }

  def force(stateId: S): StateMachine[S, E, C] = {
    currentState = stateMap.get(stateId) match {
      case Some(state) => state
      case None => throw new StateMachineException(s"State [$stateId] is undefined")
    }
    this
  }
}

object StateMachine {

  class Builder[S, E, C] {
    private[this] var machineId: String = ""
    private[this] var initState: Option[State[S, E, C]] = None
    private[this] val stateMap: concurrent.Map[S, State[S, E, C]] = concurrent.TrieMap.empty
    private[this] val transitionMap: concurrent.Map[(State[S, E, C], E), Transition[S, E, C]] = concurrent.TrieMap.empty

    def setMachineId(machineId: String): Builder[S, E, C] = {
      this.machineId = machineId
      this
    }

    def init(stateId: S): Builder[S, E, C] = {
      initState = Some(stateMap.getOrElseUpdate(stateId, new State(stateId)))
      this
    }

    def permit(from: S, to: S, on: E, when: Condition.Func[C], perform: Action.Func[S, E, C]): Builder[S, E, C] = {
      val sourceState = stateMap.getOrElseUpdate(from, new State(from))
      val targetState = stateMap.getOrElseUpdate(to, new State(to))
      transitionMap.get((sourceState, on)) match {
        case Some(_) => throw new StateMachineException(s"Event [$on] for state [$from] is redefined")
        case None => transitionMap.update((sourceState, on), new Transition(sourceState, targetState, on, when, perform))
      }
      this
    }

    def build(): StateMachine[S, E, C] = {
      if (machineId.trim.isEmpty)
        throw new StateMachineException("The id of state machine should be set up")
      initState match {
        case None => throw new StateMachineException(s"The initial state of state machine [$machineId] is undefined")
      }
      val stateMachine = new StateMachine(machineId, initState.get, stateMap, transitionMap)
      StateMachineFactory.register(machineId, stateMachine)
    }
  }

  def builder[S, E, C]() = new Builder
}
