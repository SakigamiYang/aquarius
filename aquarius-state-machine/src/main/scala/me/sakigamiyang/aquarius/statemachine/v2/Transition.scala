package me.sakigamiyang.aquarius.statemachine.v2

import scala.collection.concurrent

class Transition[S, E, C] private[Transition](private[this] final val source: State[S, E, C],
                                              private[this] final val target: State[S, E, C],
                                              private[this] final val event: E,
                                              private[this] final val condition: Condition.Func[C],
                                              private[this] final val action: Action.Func[S, E, C])
  extends Serializable with Equals {
  def getSource: State[S, E, C] = source

  def getTarget: State[S, E, C] = target

  def getEvent: E = event

  def getCondition: Condition.Func[C] = condition

  def getAction: Action.Func[S, E, C] = action

  def transit(context: C): State[S, E, C] = {
    if (condition(context)) {
      action(source.getId, target.getId, event, context)
      target
    } else {
      source
    }
  }

  override def canEqual(that: Any): Boolean = that.isInstanceOf[Transition[S, E, C]]

  override def equals(obj: Any): Boolean =
    obj match {
      case that: Transition[S, E, C] =>
        that.canEqual(this) &&
          hashCode() == that.hashCode() &&
          source == that.getSource &&
          target == that.getTarget &&
          event == that.getEvent
      case _ => false
    }

  override def hashCode(): Int = {
    var result = 1
    result += 31 * result + source.hashCode()
    result += 31 * result + target.hashCode()
    result += 31 * result + event.hashCode()
    result
  }

  override def toString: String = s"$source-[$event]->$target"
}

object Transition {

  class Builder[S, E, C](stateMap: concurrent.Map[S, State[S, E, C]]) {
    private[this] var source: State[S, E, C] = _
    private[this] var target: State[S, E, C] = _
    private[this] var event: E = _
    private[this] var condition: Condition.Func[C] = Condition.noCondition
    private[this] var action: Action.Func[S, E, C] = Action.noAction

    def from(stateId: S): Builder[S, E, C] = {
      source = stateMap.getOrElseUpdate(stateId, State.builder().setStateId(stateId).build())
      this
    }

    def to(stateId: S): Builder[S, E, C] = {
      target = stateMap.getOrElseUpdate(stateId, State.builder().setStateId(stateId).build())
      this
    }

    def on(event: E): Builder[S, E, C] = {
      this.event = event
      this
    }

    def when(condition: Condition.Func[C]): Builder[S, E, C] = {
      this.condition = condition
      this
    }

    def perform(action: Action.Func[S, E, C]): Builder[S, E, C] = {
      this.action = action
      this
    }

    def build() = new Transition(source, target, event, condition, action)
  }

  def builder[S, E, C](stateMap: concurrent.Map[S, State[S, E, C]]) = new Builder(stateMap)
}
