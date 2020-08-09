package me.sakigamiyang.aquarius.statemachine

trait Action[S, E, C] {
  def execute(from: S, to: S, event: E, context: C): Unit
}
