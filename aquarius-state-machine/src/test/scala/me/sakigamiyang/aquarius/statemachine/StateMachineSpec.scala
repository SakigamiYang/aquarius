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
      "^StateMachine\\(.*\\)$".r.pattern.matcher(s).matches shouldBe true
      s.contains("a*1->b") shouldBe true
      s.contains("a*2->c") shouldBe true
      s.contains("b*-1->a") shouldBe true
      s.contains("b*1->c") shouldBe true
      s.contains("c*-2->a") shouldBe true
      s.contains("c*-1->b") shouldBe true
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
      fs.contains(s"${indentSpaces}a*1->b") shouldBe true
      fs.contains(s"${indentSpaces}a*2->c") shouldBe true
      fs.contains(s"${indentSpaces}b*-1->a") shouldBe true
      fs.contains(s"${indentSpaces}b*1->c") shouldBe true
      fs.contains(s"${indentSpaces}c*-2->a") shouldBe true
      fs.contains(s"${indentSpaces}c*-1->b") shouldBe true

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
      fs.contains(s"${indentSpaces}a*1->b") shouldBe true
      fs.contains(s"${indentSpaces}a*2->c") shouldBe true
      fs.contains(s"${indentSpaces}b*-1->a") shouldBe true
      fs.contains(s"${indentSpaces}b*1->c") shouldBe true
      fs.contains(s"${indentSpaces}c*-2->a") shouldBe true
      fs.contains(s"${indentSpaces}c*-1->b") shouldBe true

      fs.count(_ == '\r') shouldBe 7
      fs.count(_ == '\n') shouldBe 0
    }
  }
}
