package me.sakigamiyang.aquarius.statemachine

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class StateMachineSpec extends AnyFunSpec with Matchers {
  describe("test retry") {
    it("convert to string") {
      val sm = new StateMachine[String, Int]("a")
        .builder()
        .configure("a")
        .permit(1, "b")
        .permit(2, "c")
        .end()
        .configure("b")
        .permit(-1, "a")
        .permit(1, "c")
        .end()
        .configure("c")
        .permit(-2, "a")
        .permit(-1, "b")
        .end()
        .build()
      sm.toString shouldBe "StateMachine(a*1->b,a*2->c,b*-1->a,b*1->c,c*-2->a,c*-1->b)"
    }

    it("convert to formatted string") {
      val sm = new StateMachine[String, Int]("a")
        .builder()
        .configure("a")
        .permit(1, "b")
        .permit(2, "c")
        .end()
        .configure("b")
        .permit(-1, "a")
        .permit(1, "c")
        .end()
        .configure("c")
        .permit(-2, "a")
        .permit(-1, "b")
        .end()
        .build()
      sm.toFormattedString() shouldBe "StateMachine(\n  a*1->b,\n  a*2->c,\n  b*-1->a,\n  b*1->c,\n  c*-2->a,\n  c*-1->b\n)"
    }
  }
}
