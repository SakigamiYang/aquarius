package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine.Condition

trait On[S, E, C] extends When[S, E, C] {
  def when(condition: Condition[C]): When[S, E, C]
}
