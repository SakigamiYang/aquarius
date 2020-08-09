package me.sakigamiyang.aquarius.statemachine

trait StateContext[S, E, C] {
  def getTransition: Transition[S, E, C]

  def getStateMachine: StateMachine[S, E, C]
}
