package me.sakigamiyang.aquarius.statemachine.v2

object Condition {
  type Func[C] = C => Boolean

  def noCondition[C]: Func[C] = (_: C) => true
}
