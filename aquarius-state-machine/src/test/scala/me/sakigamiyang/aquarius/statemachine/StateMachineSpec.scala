package me.sakigamiyang.aquarius.statemachine

import me.sakigamiyang.aquarius.common.io.FileUtils
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class StateMachineSpec extends AnyFunSpec with Matchers {
  describe("test retry") {
    it("convert to string") {
      val sm = StateMachine
        .builder()
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      val s = sm.toString
      println(s)
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
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
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

      FileUtils.NewLines.SYSTEM_DEPENDENT.getContent.foreach(c => fs.count(_ == c) shouldBe 7)
    }

    it("convert to formatted string with newLine=\\r and indent=8") {
      val sm = StateMachine
        .builder()
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      val indent = 8
      val fs = sm.toFormattedString(FileUtils.NewLines.CLASSIC_MACOS, indent)

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
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      sm.fire(1).fire(1).fire(-2)
      sm.getInitialState shouldBe "a"
    }

    it("get current state") {
      val sm = StateMachine
        .builder()
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      sm.getInitialState shouldBe "a"

      sm.fire(1).fire(1).fire(-1)
      sm.getCurrentState shouldBe "b"
    }

    it("get last state after initialized") {
      val sm = StateMachine
        .builder()
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      val thrown = intercept[StateMachineException] {
        sm.getLastState
      }
      thrown.getMessage shouldBe "StateMachine is just initialized without having last state"
    }

    it("get last state after fire trigger") {
      val sm = StateMachine
        .builder()
        .initState("a")
        .addState("a")
        .addState("b")
        .addState("c")
        .addTransmission("a", 1, "b")
        .addTransmission("a", 2, "c")
        .addTransmission("b", -1, "a")
        .addTransmission("b", 1, "c")
        .addTransmission("c", -1, "b")
        .addTransmission("c", -2, "a")
        .build()

      sm.fire(2).getLastState shouldBe "a"
    }
  }
}
