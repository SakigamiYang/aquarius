package me.sakigamiyang.aquarius.statemachine

trait State[S, E, C] {
  def getId: S

  def addTransition(event: E, target: State[S, E, C], transitionType: TransitionType.Value): Transition[S, E, C]

  def getTransition(event: E): Option[Transition[S, E, C]]

  def getTransitions: Iterable[Transition[S, E, C]]
}
