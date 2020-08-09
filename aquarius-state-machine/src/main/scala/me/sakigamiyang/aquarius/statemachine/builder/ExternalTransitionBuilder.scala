package me.sakigamiyang.aquarius.statemachine.builder

trait ExternalTransitionBuilder[S, E, C] {
  def from(stateId: S): From[S, E, C]
}
