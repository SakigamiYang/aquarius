package me.sakigamiyang.aquarius.statemachine

/**
 * Transition from one state to another.
 *
 * @param from         state
 * @param to           next state
 * @param on           trigger
 * @param transitEvent event to call
 * @tparam TState   type of state
 * @tparam TTrigger type of trigger
 */
case class Transition[TState, TTrigger](from: State[TState], to: State[TState], on: TTrigger, transitEvent: TTransitEvent)
