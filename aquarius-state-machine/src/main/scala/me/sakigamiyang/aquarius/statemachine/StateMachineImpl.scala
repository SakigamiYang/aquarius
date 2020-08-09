package me.sakigamiyang.aquarius.statemachine

import scala.collection.concurrent

class StateMachineImpl[S, E, C](private[this] final val stateMap: concurrent.Map[S, State[S, E, C]])
  extends StateMachine[S, E, C] with Serializable {
  private[this] var machineId: String = _
  private[this] var ready: Boolean = _

  override def fireEvent(sourceStateId: S, event: E, context: C): S = {
    checkReady()
    val sourceState = getState(sourceStateId)
    doTransition(sourceState, event, context).getId
  }

  private[this] def checkReady(): Unit =
    if (!ready)
      throw new StateMachineException("State machine is not built yet, can not work")

  private[this] def doTransition(sourceState: State[S, E, C], event: E, context: C): State[S, E, C] =
    sourceState.getTransition(event) match {
      case Some(value) => value.transit(context)
      case None => sourceState
    }

  private[this] def getState(currentStateId: S): State[S, E, C] = StateHelper.getState(stateMap, currentStateId)

  override def getMachineId: String = machineId

  def setMachineId(machineId: String): Unit = this.machineId = machineId

  def setReady(ready: Boolean): Unit = this.ready = ready
}
