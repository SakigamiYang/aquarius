package me.sakigami_yang.aquarius.common.exception

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PackageSpec extends AnyFunSpec with Matchers {
  describe("test retry") {
    it("StackTrace to string") {
      val ex1 = new NumberFormatException
      val ex2 = new RuntimeException
      ex2.addSuppressed(ex1)
      val stackTrace = stackTraceToString(ex2)
      stackTrace.startsWith("java.lang.RuntimeException") shouldBe true
      stackTrace.contains("Suppressed: java.lang.NumberFormatException") shouldBe true
    }
  }
}
