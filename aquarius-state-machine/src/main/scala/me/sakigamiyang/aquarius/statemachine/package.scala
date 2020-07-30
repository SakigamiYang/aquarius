package me.sakigamiyang.aquarius

package object statemachine {
  /**
   * Event to call on transiting.
   */
  type TTransitEvent = Seq[Any] => Unit
  /**
   * Event to call when onto a state.
   */
  type TOntoEvent = () => Unit

  val defaultTransitEvent: TTransitEvent = (args: Seq[Any]) => Unit

  val defaultOntoEvent: TOntoEvent = () => ()
}
