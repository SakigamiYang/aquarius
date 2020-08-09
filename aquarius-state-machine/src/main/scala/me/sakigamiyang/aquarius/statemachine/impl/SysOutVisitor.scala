package me.sakigamiyang.aquarius.statemachine.impl

import me.sakigamiyang.aquarius.statemachine.{State, StateMachine, Visitor}

case class SysOutVisitor() extends Visitor {
  override def visitOnEntry(stateMachine: StateMachine[_, _, _]): String = {
    val entry = s"-----StateMachine:${stateMachine.getMachineId}-------"
    println(entry)
    entry
  }

  override def visitOnExit(visitable: StateMachine[_, _, _]): String = {
    val exit = s"------------------------"
    println(exit)
    exit
  }

  override def visitOnEntry(state: State[_, _, _]): String = {
    val sb = new StringBuilder
    sb.append(s"State:${state.getId}").append(LF)
    state.getTransitions.foreach(transition => sb.append(s"    Transition:$transition").append(LF))
    
    val result = sb.result()
    println(result)
    result
  }

  override def visitOnExit(state: State[_, _, _]): String = ""
}
