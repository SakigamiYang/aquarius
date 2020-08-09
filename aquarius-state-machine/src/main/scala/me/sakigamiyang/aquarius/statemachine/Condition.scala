package me.sakigamiyang.aquarius.statemachine

trait Condition[C] {
  def isSatisfied(context: C): Boolean

  def name: String = getClass.getSimpleName
}
