package me.sakigamiyang.aquarius.common.util

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class RetrySpec extends AnyFunSpec with Matchers {
  var caseNo = 0

  describe("test retry") {
    it("retry for function that succeeds") {
      caseNo += 1

      var i = 0
      val result = Retry(5) {
        i += 1
        println(s"try #$caseNo-$i")
        Success(1)
      }
      result.isSuccess shouldBe true
      result shouldBe Success(1)
    }

    it("retry without delay for function that fails") {
      caseNo += 1
      val ex = new RuntimeException

      var i = 0
      val result = Retry(5) {
        i += 1
        println(s"try #$caseNo-$i")
        Failure(ex)
      }
      result.isFailure shouldBe true
      result shouldBe Failure(ex)
    }

    it("retry with delay for function that fails") {
      caseNo += 1
      val ex = new RuntimeException

      var i = 0
      val result = Retry(5, 1000) {
        i += 1
        println(s"try #$caseNo-$i")
        Failure(ex)
      }
      result.isFailure shouldBe true
      result shouldBe Failure(ex)
    }

    it("retry with delay for function that fails, then print error message") {
      caseNo += 1
      val ex = new RuntimeException(s"exception for case $caseNo")

      var i = 0
      val result = Retry(5, 3000, s => println(s"!!!!! $s !!!!!")) {
        i += 1
        println(s"try #$caseNo-$i")
        Failure(ex)
      }
      result.isFailure shouldBe true
      result shouldBe Failure(ex)
    }
  }
}
