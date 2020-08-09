package me.sakigamiyang.aquarius.statemachine

class TransitionImpl[S, E, C](private[this] val source: State[S, E, C],
                              private[this] val target: State[S, E, C],
                              private[this] val event: E,
                              private[this] val transitionType: TransitionType.Value)
  extends Transition[S, E, C] with Serializable with Equals {
  private[this] var condition: Condition[C] = _
  private[this] var action: Action[S, E, C] = _

  override def getSource: State[S, E, C] = source

  override def getTarget: State[S, E, C] = target

  override def getEvent: E = event

  override def getCondition: Condition[C] = condition

  override def setCondition(condition: Condition[C]): Unit = this.condition = condition

  override def getAction: Action[S, E, C] = action

  override def setAction(action: Action[S, E, C]): Unit = this.action = action

  override def transit(context: C): State[S, E, C] = {
    verify()
    if (condition == null || condition.isSatisfied(context)) {
      if (action != null) {
        action.execute(source.getId, target.getId, event, context)
      }
      return target
    }
    source
  }

  override def toString: String = s"$source-[$event,$transitionType]->$target"

  override def canEqual(that: Any): Boolean = that.isInstanceOf[TransitionImpl[S, E, C]]

  override def equals(obj: Any): Boolean =
    obj match {
      case that: TransitionImpl[S, E, C] =>
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

  override def verify(): Unit =
    if (transitionType == TransitionType.INTERNAL && source != target)
      throw new StateMachineException(s"Internal transition source state [$source] and target state [$target] must be same")
}
