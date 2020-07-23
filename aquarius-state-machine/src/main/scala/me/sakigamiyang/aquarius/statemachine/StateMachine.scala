package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.logging.Logging

import scala.collection.{immutable, mutable}

class StateMachine[TState, TTrigger](initialState: TState) extends Serializable with Logging {

  private[this] var stateMachineConfig = immutable.ListMap[State[TState], immutable.ListMap[TTrigger, State[TState]]]()

  class StateConfig(state: State[TState], builder: Builder) extends Serializable with Logging {
    private[this] var stateConfig = immutable.ListMap[TTrigger, State[TState]]()

    def permit(trigger: TTrigger, nextState: TState): StateConfig = {
      require(trigger != null)
      require(nextState != null)
      if (stateConfig contains trigger)
        throw new IllegalArgumentException(s"Trigger '$trigger' have already been permitted for state '$state'")
      stateConfig = stateConfig.updated(trigger, new State(nextState))
      this
    }

    def end(): Builder = {
      builder.internalStateMachineConfig = builder.internalStateMachineConfig.updated(state, stateConfig)
      builder
    }
  }

  class Builder(stateMachine: StateMachine[TState, TTrigger]) extends Serializable with Logging {
    private[statemachine] var internalStateMachineConfig = immutable.ListMap[State[TState], immutable.ListMap[TTrigger, State[TState]]]()

    def configure(state: TState): StateConfig = {
      require(state != null)
      val s = new State(state)
      if (internalStateMachineConfig contains s)
        throw new IllegalArgumentException(s"State '$state' have already been configured")
      new StateConfig(s, this)
    }

    def build(): StateMachine[TState, TTrigger] = {
      stateMachineConfig = internalStateMachineConfig
      stateMachine
    }
  }

  def builder(): Builder = new Builder(this)

  def toFormattedString(newline: String = "\n", indent: Int = 2): String = {
    require(newline != null)
    require(indent >= 0)
    val indentSpaces = " " * (if (newline.isEmpty) 0 else indent)
    val sb = new mutable.StringBuilder
    sb ++= "StateMachine(" ++= newline
    for ((state, mapTriggerState) <- stateMachineConfig) {
      for ((trigger, nextState) <- mapTriggerState) {
        sb ++= indentSpaces ++= s"$state*$trigger->$nextState,"
        sb ++= newline
      }
    }
    sb.delete(sb.length - 1 - newline.length, sb.length)
    sb ++= newline ++= ")"
    sb.result()
  }

  override def toString: String = toFormattedString(newline = "")
}
