package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine._

import scala.collection.concurrent

class StateMachineBuilderImpl[S, E, C] private[StateMachineBuilderImpl]
(private final val stateMap: concurrent.Map[S, State[S, E, C]])
  extends StateMachineBuilder[S, E, C] with Serializable {

  private[this] final val stateMachine = new StateMachineImpl[S, E, C](stateMap)

  override def externalTransition: ExternalTransitionBuilder[S, E, C] =
    new TransitionBuilderImpl[S, E, C](stateMap, TransitionType.EXTERNAL)

  override def externalTransitions: ExternalTransitionsBuilder[S, E, C] =
    new TransitionsBuilderImpl[S, E, C](stateMap, TransitionType.EXTERNAL)

  override def internalTransition: InternalTransitionBuilder[S, E, C] =
    new TransitionBuilderImpl[S, E, C](stateMap, TransitionType.INTERNAL)

  override def build(machineId: String): StateMachine[S, E, C] = {
    stateMachine.setMachineId(machineId)
    stateMachine.setReady(true)
    StateMachineFactory.register(stateMachine)
    stateMachine
  }
}

object StateMachineBuilderImpl {
  def create[S, E, C](): StateMachineBuilderImpl[S, E, C] = {
    val stateMap: concurrent.Map[S, State[S, E, C]] = concurrent.TrieMap.empty
    new StateMachineBuilderImpl[S, E, C](stateMap)
  }
}
