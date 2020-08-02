package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.io.NewLines
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class StateMachineSpec extends AnyFunSpec with Matchers {
  describe("test state machine") {
    it("convert to string") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      val s = sm.toString
      "^StateMachine\\(.*\\)$".r.pattern.matcher(s).matches shouldBe true
      s.contains("State(a)*1->State(b)") shouldBe true
      s.contains("State(a)*2->State(c)") shouldBe true
      s.contains("State(b)*-1->State(a)") shouldBe true
      s.contains("State(b)*1->State(c)") shouldBe true
      s.contains("State(c)*-2->State(a)") shouldBe true
      s.contains("State(c)*-1->State(b)") shouldBe true
    }

    it("convert to formatted string with default parameters") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      val fs = sm.toFormattedString()

      "^StateMachine\\([\\s\\S]*\\)$".r.pattern.matcher(fs).matches shouldBe true

      val indentSpaces = " " * 2
      fs.contains(s"${indentSpaces}State(a)*1->State(b)") shouldBe true
      fs.contains(s"${indentSpaces}State(a)*2->State(c)") shouldBe true
      fs.contains(s"${indentSpaces}State(b)*-1->State(a)") shouldBe true
      fs.contains(s"${indentSpaces}State(b)*1->State(c)") shouldBe true
      fs.contains(s"${indentSpaces}State(c)*-2->State(a)") shouldBe true
      fs.contains(s"${indentSpaces}State(c)*-1->State(b)") shouldBe true

      NewLines.SYSTEM_DEPENDENT.value.foreach(c => fs.count(_ == c) shouldBe 7)
    }

    it("convert to formatted string with newLine=\\r and indent=8") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      val indent = 8
      val fs = sm.toFormattedString(NewLines.CLASSIC_MACOS, indent)

      "^StateMachine\\([\\s\\S]*\\)$".r.pattern.matcher(fs).matches shouldBe true

      val indentSpaces = " " * indent
      fs.contains(s"${indentSpaces}State(a)*1->State(b)") shouldBe true
      fs.contains(s"${indentSpaces}State(a)*2->State(c)") shouldBe true
      fs.contains(s"${indentSpaces}State(b)*-1->State(a)") shouldBe true
      fs.contains(s"${indentSpaces}State(b)*1->State(c)") shouldBe true
      fs.contains(s"${indentSpaces}State(c)*-2->State(a)") shouldBe true
      fs.contains(s"${indentSpaces}State(c)*-1->State(b)") shouldBe true

      fs.count(_ == '\r') shouldBe 7
      fs.count(_ == '\n') shouldBe 0
    }

    it("get initial state") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      sm.fire(1).fire(1).fire(-2)
      sm.getInitialState shouldBe "a"
    }

    it("get current state") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      sm.getInitialState shouldBe "a"

      sm.fire(1).fire(1).fire(-1)
      sm.getCurrentState shouldBe "b"
    }

    it("get last state after initialized") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      val thrown = intercept[StateMachineException] {
        sm.getLastState
      }
      thrown.getMessage shouldBe "StateMachine is just initialized without having last state"
    }

    it("get last state after fire trigger") {
      val sm = StateMachine
        .builder()
        .init("a")
        .permit("a", "b", 1)
        .permit("a", "c", 2)
        .permit("b", "a", -1)
        .permit("b", "c", 1)
        .permit("c", "b", -1)
        .permit("c", "a", -2)
        .build()

      sm.fire(2).getLastState shouldBe "a"
    }

    it("fire trigger") {
      val sm = StateMachine.builder()
        .init(1)
        .permit(1, 2, 1)
        .build()

      sm.getCurrentState shouldBe 1
      sm.fire(1)
      sm.getCurrentState shouldBe 2
    }

    it("forced to set to some state") {
      val sm = StateMachine.builder()
        .init(1)
        .permit(1, 2, 1)
        .permit(2, 3, 1)
        .build()

      sm.getCurrentState shouldBe 1
      sm.fire(1).forceState(3)
      sm.getCurrentState shouldBe 3
    }

    it("run transition action and onto action with no parameters") {
      var s = ""

      val sm = StateMachine.builder()
        .init(1)
        .permit(1, 2, 1, (_: Seq[Any]) => s += "[1->2]")
        .permit(2, 3, 1, (_: Seq[Any]) => s += "[2->3]")
        .onto(2, () => s += "[onto 2]")
        .onto(3, () => s += "[onto 3]")
        .build()

      sm.fire(1).forceState(3)
      s.contains("[1->2]") shouldBe true
      s.contains("[onto 2]") shouldBe true
      s.contains("[onto 3]") shouldBe true
    }

    it("run transition action with parameters, and 'forceState' do not trigger action") {
      var s = ""

      def funcOneToTwo(args: Seq[Any]): Unit = {
        s += s"[1->2](${args.head},${args.tail.head})"
      }

      def funcTwoToThree(args: Seq[Any]): Unit = {
        s += s"[2->3](${args.head},${args.tail.head},${args.tail.tail.head})"
      }

      val sm = StateMachine.builder()
        .init(1)
        .permit(1, 2, 1, funcOneToTwo)
        .permit(2, 3, 1, funcTwoToThree)
        .build()

      sm.fire(1, "a", 22).forceState(3)
      s.contains("[1->2](a,22)") shouldBe true
      s.contains("[2->3]") shouldBe false
    }

    it("check unreachable states 1") {
      val errorMsg = intercept[StateMachineException] {
        StateMachine
          .builder()
          .init("a")
          .permit("a", "b", 1)
          .permit("a", "c", 2)
          .permit("b", "a", -1)
          .permit("b", "c", 1)
          .permit("c", "b", -1)
          .permit("c", "a", -2)
          .permit("d", "a", -3)
          .build()
      }.getMessage

      errorMsg shouldBe "States [State(d)] are unreachable from initial state"

    }

    it("check unreachable states 2") {
      val errorMsg = intercept[StateMachineException] {
        StateMachine
          .builder()
          .init("a")
          .permit("a", "b", 1)
          .permit("a", "c", 2)
          .permit("b", "a", -1)
          .permit("b", "c", 1)
          .permit("c", "b", -1)
          .permit("c", "a", -2)
          .permit("d", "e", 1)
          .permit("e", "f", 1)
          .permit("f", "d", -2)
          .build()
      }.getMessage

      val matcher = "\\[.+]".r.findFirstMatchIn(errorMsg).get.group(0)
      matcher.contains("State(d)") shouldBe true
      matcher.contains("State(e)") shouldBe true
      matcher.contains("State(f)") shouldBe true
    }
  }
}
