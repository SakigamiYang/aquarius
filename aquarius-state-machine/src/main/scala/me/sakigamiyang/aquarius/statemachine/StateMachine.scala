package me.sakigamiyang.aquarius.statemachine

trait StateMachine[S, E, C] extends Visitable {
  def fireEvent(sourceState: S, event: E, context: C)

  def getMachineId: String

  def showStateMachine(): Unit
}
