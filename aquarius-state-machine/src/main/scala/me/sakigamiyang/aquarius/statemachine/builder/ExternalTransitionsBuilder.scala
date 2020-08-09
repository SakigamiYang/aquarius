package me.sakigamiyang.aquarius.statemachine.builder

trait ExternalTransitionsBuilder[S, E, C] {
  def fromAmong(stateIds: S*): From[S, E, C]
}
