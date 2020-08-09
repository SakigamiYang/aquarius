package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.statemachine.impl.StateMachineException

import scala.collection.concurrent

/**
 * StateMachineFactory
 */
object StateMachineFactory {
  private[this] val stateMachineMap: concurrent.Map[String, StateMachine[_ <: Any, _ <: Any, _ <: Any]] =
    concurrent.TrieMap.empty

  /**
   * Register state machine with its id.
   *
   * @param stateMachine state machine
   * @tparam S type of State
   * @tparam E type of Event
   * @tparam C type of Context
   */
  def register[S, E, C](stateMachine: StateMachine[S, E, C]): Unit = {
    val machineId = stateMachine.getMachineId
    stateMachineMap.get(machineId) match {
      case Some(_) => throw new StateMachineException(s"The state machine with id [$machineId] is already built")
      case None => stateMachineMap.update(machineId, stateMachine)
    }
  }

  /**
   * Get state machine by id.
   *
   * @param machineId state machine id
   * @tparam S type of State
   * @tparam E type of Event
   * @tparam C type of Context
   * @return
   */
  def get[S, E, C](machineId: String): StateMachine[S, E, C] = {
    stateMachineMap.get(machineId) match {
      case Some(value) => value.asInstanceOf[StateMachine[S, E, C]]
      case None => throw new StateMachineException(s"There is no stateMachine instance for $machineId")
    }
  }
}
