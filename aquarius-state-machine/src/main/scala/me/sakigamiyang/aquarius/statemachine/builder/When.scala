package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine.Action

trait When[S, E, C] {
  def perform(action: Action[S, E, C]): Unit
}
