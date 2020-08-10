package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine._

import scala.collection.concurrent

class TransitionBuilderImpl[S, E, C](private[this] final val stateMap: concurrent.Map[S, State[S, E, C]],
                                     private[this] final val transitionType: TransitionType.Value)
  extends ExternalTransitionBuilder[S, E, C]
    with InternalTransitionBuilder[S, E, C]
    with From[S, E, C]
    with To[S, E, C]
    with On[S, E, C] {

  protected var source: State[S, E, C] = _
  protected var target: State[S, E, C] = _
  private[this] var transition: Transition[S, E, C] = _

  override def from(stateId: S): From[S, E, C] = {
    source = StateHelper.getState(stateMap, stateId)
    this
  }

  override def to(stateId: S): To[S, E, C] = {
    target = StateHelper.getState(stateMap, stateId)
    this
  }

  override def withIn(stateId: S): To[S, E, C] = {
    val s = StateHelper.getState(stateMap, stateId)
    source = s
    target = s
    this
  }

  override def on(event: E): On[S, E, C] = {
    transition = source.addTransition(event, target, transitionType)
    this
  }

  override def when(condition: Condition[C]): When[S, E, C] = {
    transition.setCondition(condition)
    this
  }

  override def perform(action: Action[S, E, C]): Unit =
    transition.setAction(action)
}
