package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.logging.Logging

import scala.reflect.runtime.universe._

class State[TState](private val state: TState) extends Serializable with Logging {
  private var entryActions: Seq[(Type, Any)] => Unit = _
  private var exitActions: Seq[(Type, Any)] => Unit = _

  def getState: TState = state

  override def toString: String = state.toString
}
