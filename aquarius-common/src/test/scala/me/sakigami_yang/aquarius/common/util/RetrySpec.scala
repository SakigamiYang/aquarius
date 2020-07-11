package me.sakigami_yang.aquarius.common.util

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class RetrySpec extends AnyFunSpec with Matchers {
  var caseNo = 0

  describe("test retry") {
    it("retry ok") {
      caseNo += 1
      var i = 0
      Retry(5) {
        i += 1
        println(s"try #$caseNo-$i")
        1
      } shouldBe 1
    }

    it("retry without delay") {
      caseNo += 1
      val ex = new RuntimeException
      intercept[RuntimeException] {
        var i = 0
        Retry(5) {
          i += 1
          println(s"try #$caseNo-$i")
          throw ex
        }
      } shouldBe ex
    }

    it("retry with delay") {
      caseNo += 1
      val ex = new RuntimeException
      intercept[RuntimeException] {
        var i = 0
        Retry(5, 1000.millis) {
          i += 1
          println(s"try #$caseNo-$i")
          throw ex
        }
      } shouldBe ex
    }
  }
}
