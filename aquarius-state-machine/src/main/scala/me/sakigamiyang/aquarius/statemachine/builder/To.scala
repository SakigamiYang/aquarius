package me.sakigamiyang.aquarius.statemachine.builder

trait To[S, E, C] {
  def on(event: E): On[S, E, C]
}
