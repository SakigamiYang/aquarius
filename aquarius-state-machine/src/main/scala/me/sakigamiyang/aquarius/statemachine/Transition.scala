package me.sakigamiyang.aquarius.statemachine

class Transition[S, E, C] private[statemachine](private[this] final val source: State[S, E, C],
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
