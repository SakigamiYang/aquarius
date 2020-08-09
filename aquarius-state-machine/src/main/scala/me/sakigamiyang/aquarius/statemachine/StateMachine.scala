package me.sakigamiyang.aquarius.statemachine

trait StateMachine[S, E, C] {
  def fireEvent(sourceStateId: S, event: E, context: C): S

  def getMachineId: String
}
