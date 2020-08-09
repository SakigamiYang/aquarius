package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.statemachine.impl.TransitionType

trait State[S, E, C] extends Visitable {
  def getId: S

  def addTransition(event: E, target: State[S, E, C], transitionType: TransitionType.Value): Transition[S, E, C]

  def getTransition(event: E): Option[Transition[S, E, C]]

  def getTransitions: Iterable[Transition[S, E, C]]
}
