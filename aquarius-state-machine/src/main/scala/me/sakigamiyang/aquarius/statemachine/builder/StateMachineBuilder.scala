package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine.StateMachine

trait StateMachineBuilder[S, E, C] {
  def externalTransition: ExternalTransitionBuilder[S, E, C]

  def externalTransitions: ExternalTransitionsBuilder[S, E, C]

  def internalTransition: InternalTransitionBuilder[S, E, C]

  def build(machineId: String): StateMachine[S, E, C]
}
