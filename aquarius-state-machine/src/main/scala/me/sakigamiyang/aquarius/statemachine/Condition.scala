package me.sakigamiyang.aquarius.statemachine

object Condition {
  type Func[C] = C => Boolean

  def noCondition[C]: Func[C] = (_: C) => true
}
