package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.statemachine.impl.TransitionType

trait Transition[S, E, C] {
  def getSource: State[S, E, C]

  def getTarget: State[S, E, C]

  def getEvent: E

  def getCondition: Condition[C]

  def setCondition(condition: Condition[C]): Unit

  def getAction: Action[S, E, C]

  def setAction(action: Action[S, E, C]): Unit

  def transit(context: C): State[S, E, C]

  def verify(): Unit
}
