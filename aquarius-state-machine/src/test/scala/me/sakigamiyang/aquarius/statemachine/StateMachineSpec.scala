package me.sakigamiyang.aquarius.statemachine

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class StateMachineSpec extends AnyFunSpec with Matchers {
  describe("test retry") {
    it("convert to string") {
      val sm = StateMachine
        .builder()
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .initState("a")
        .build()
      //      sm.toString shouldBe "StateMachine(a*1->b,a*2->c,b*-1->a,b*1->c,c*-2->a,c*-1->b)"
      println(sm.toString)
    }

    it("convert to formatted string") {
      val sm = StateMachine
        .builder()
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .initState("a")
        .build()
      //      sm.toFormattedString() shouldBe "StateMachine(\n  a*1->b,\n  a*2->c,\n  b*-1->a,\n  b*1->c,\n  c*-2->a,\n  c*-1->b\n)"
      println(sm.toFormattedString())
    }
  }
}
