package me.sakigamiyang.aquarius.statemachine

trait Visitor {
  final val LF = '\n'

  def visitOnEntry(stateMachine: StateMachine[_ <: Any, _ <: Any, _ <: Any]): String

  def visitOnExit(stateMachine: StateMachine[_ <: Any, _ <: Any, _ <: Any]): String

  def visitOnEntry(state: State[_ <: Any, _ <: Any, _ <: Any]): String

  def visitOnExit(state: State[_ <: Any, _ <: Any, _ <: Any]): String
}
