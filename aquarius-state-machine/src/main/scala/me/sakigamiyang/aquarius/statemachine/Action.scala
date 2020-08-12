package me.sakigamiyang.aquarius.statemachine

object Action {
  type Func[S, E, C] = (S, S, E, C) => Unit

  def noAction[S, E, C]: Func[S, E, C] = (_: S, _: S, _: E, _: C) => ()
}
