package me.sakigamiyang.aquarius.statemachine.builder

trait From[S, E, C] {
  def to(stateId: S): To[S, E, C]
}
