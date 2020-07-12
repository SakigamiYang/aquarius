package me.sakigamiyang.aquarius.common.io

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class StreamIOSpec extends AnyFunSpec with Matchers {
  describe("test StreamIO") {
    it("copy the entire stream") {
      val buf = new Array[Byte](2048)
      (new Random).nextBytes(buf)
      val bis = new java.io.ByteArrayInputStream(buf)
      val bos = new java.io.ByteArrayOutputStream()
      StreamIO.copy(bis, bos)
      bos.toByteArray.toSeq shouldBe buf.toSeq
    }

    it("produce empty streams from empty streams") {
      val bis = new java.io.ByteArrayInputStream(new Array[Byte](0))
      val bos = new java.io.ByteArrayOutputStream()
      StreamIO.copy(bis, bos)
      bos.size shouldBe 0
    }
  }
}
