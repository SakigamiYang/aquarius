package me.sakigamiyang.aquarius.statemachine.builder

class TransitionBuilderImpl[S, E, C]
  extends ExternalTransitionBuilder[S, E, C]
    with InternalTransitionBuilder[S, E, C]
    with From[S, E, C]
    with To[S, E, C]
    with On[S, E, C] {

}
