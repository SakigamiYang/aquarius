package me.sakigamiyang.aquarius.statemachine.builder

import me.sakigamiyang.aquarius.statemachine._

import scala.collection.{concurrent, mutable}

class TransitionsBuilderImpl[S, E, C](private[this] final val stateMap: concurrent.Map[S, State[S, E, C]],
                                      private[this] final val transitionType: TransitionType.Value)
  extends TransitionBuilderImpl[S, E, C](stateMap, transitionType)
    with ExternalTransitionsBuilder[S, E, C] {

  private[this] val sources: mutable.Seq[State[S, E, C]] = mutable.Seq.empty
  private[this] val transitions: mutable.Seq[Transition[S, E, C]] = mutable.Seq.empty

  override def fromAmong(stateIds: S*): From[S, E, C] = {
    stateIds.foreach(stateId => sources.update(0, StateHelper.getState(stateMap, stateId)))
    this
  }

  override def on(event: E): On[S, E, C] = {
    sources.foreach(source => {
      val transition = source.addTransition(event, target, transitionType)
      transitions.update(0, transition)
    })
    this
  }

  override def when(condition: Condition[C]): When[S, E, C] = {
    transitions.foreach(transition => transition.setCondition(condition))
    this
  }

  override def perform(action: Action[S, E, C]): Unit =
    transitions.foreach(transition => transition.setAction(action))
}
