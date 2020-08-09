package me.sakigamiyang.aquarius.statemachine

trait Visitable {
  def accept(visitor: Visitor): String
}
