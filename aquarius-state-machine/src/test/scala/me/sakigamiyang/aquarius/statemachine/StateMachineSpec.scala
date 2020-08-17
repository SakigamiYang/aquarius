package me.sakigamiyang.aquarius.statemachine

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class StateMachineSpec extends AnyFunSpec with Matchers {
  describe("test state machine") {
    it("build a state machine") {
      val stateMachineId = "first state machine"
      val stateMachine = StateMachine.builder()
        .setMachineId(stateMachineId)
        .permit("a", "b", 1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("a", "c", 2, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("b", "a", -1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("b", "c", 1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("c", "a", -2, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("c", "b", -1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .init("a")
        .build()

      stateMachine.getMachineId shouldBe stateMachineId
      StateMachineFactory.get(stateMachineId) shouldBe stateMachine
    }

    it("transit between states") {
      val stateMachineId = "second state machine"
      val stateMachine = StateMachine.builder()
        .setMachineId(stateMachineId)
        .permit("a", "b", 1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("a", "c", 2, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("b", "a", -1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("b", "c", 1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("c", "a", -2, Condition.noCondition[String], Action.noAction[String, Int, String])
        .permit("c", "b", -1, Condition.noCondition[String], Action.noAction[String, Int, String])
        .init("a")
        .build()

      stateMachine.getCurrentState shouldBe "a"
      stateMachine.fire(1, "").getCurrentState shouldBe "b"
      stateMachine.fire(1, "").fire(-1, "").fire(-1, "").getCurrentState shouldBe "a"
      stateMachine.force("b").getCurrentState shouldBe "b"
    }

    it("call events") {
      val stateMachineId = "third state machine"
      var s = ""
      var s2 = ""

      val actionAToB = (sourceId: String, targetId: String, event: Int, context: String) =>
        s += s"$sourceId-$targetId[$event,$context]\n"
      val actionBToC = (sourceId: String, targetId: String, event: Int, context: String) =>
        s += s"$sourceId-$targetId[$event,$context]\n"
      val actionCToA = (sourceId: String, targetId: String, event: Int, context: String) =>
        s += s"$sourceId-$targetId[$event,$context]\n"

      val conditionBtoC = (context: String) => {
        val result = context.startsWith("{b-c}")
        s2 += (if (result) "" else "not ") + "satisfied condition b to c\n"
        result
      }
      val conditionCToA = (context: String) => {
        val result = context.startsWith("{c-a}")
        s2 += (if (result) "" else "not ") + "satisfied condition c to a\n"
        result
      }

      val stateMachine = StateMachine.builder()
        .setMachineId(stateMachineId)
        .permit("a", "b", 1, Condition.noCondition[String], actionAToB)
        .permit("b", "c", 1, conditionBtoC, actionBToC)
        .permit("c", "a", 1, conditionCToA, actionCToA)
        .init("a")
        .build()

      stateMachine.fire(1, "{a-b}").fire(1, "{b-c}").fire(1, "!{c-a}")
      s shouldBe "a-b[1,{a-b}]\nb-c[1,{b-c}]\n"
      s2 shouldBe "satisfied condition b to c\nnot satisfied condition c to a\n"
      stateMachine.getCurrentState shouldBe "c"
    }
  }
}
