package me.sakigamiyang.aquarius.statemachine.builder

trait InternalTransitionBuilder[S, E, C] {
  def withIn(stateId: S): To[S, E, C]
}
