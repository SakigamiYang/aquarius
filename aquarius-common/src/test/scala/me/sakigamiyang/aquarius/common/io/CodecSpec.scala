package me.sakigamiyang.aquarius.common.io

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import Codec._

class CodecSpec extends AnyFunSpec with Matchers {
  describe("test codec") {
    it("string encoder run OK") {
      val longString =
        "A string that is really really really really really really long and has more than 76 characters"
      val result =
        "QSBzdHJpbmcgdGhhdCBpcyByZWFsbHkgcmVhbGx5IHJlYWxseSByZWFsbHkgcmVhbGx5IHJlYWxseSBsb25nIGFuZCBoYXMgbW9yZSB0aGFuIDc2IGNoYXJhY3RlcnM="
      Base64StringEncoder.encode(longString.getBytes) shouldBe result
      new String(Base64StringEncoder.decode(result)) shouldBe longString
    }

    it("url safe string encoder run OK") {
      val urlUnsafeBytes: Array[Byte] = Array(-1.toByte, -32.toByte)
      val resultUnsafe = "/+A"
      val resultSafe = "_-A"

      Base64UrlSafeStringEncoder.encode(urlUnsafeBytes) shouldBe resultSafe
      Base64UrlSafeStringEncoder.decode(resultSafe) shouldBe urlUnsafeBytes

      intercept[IllegalArgumentException] {
        Base64UrlSafeStringEncoder.decode(resultUnsafe)
      }
    }

    it("gzip string encoder run OK") {
      val gse = new GZIPStringEncoder {}

      def testCodec(str: String): Unit = {
        str shouldBe gse.decodeString(gse.encodeString(str))
      }

      testCodec("a")
      testCodec("\n\t\n\t\n\n\n\n\t\n\nt\n\t\n\t\n\tn\t\nt\nt\nt\nt\nt\nt\tn\nt\nt\n\t\nt\n")
      testCodec("aosnetuhsaontehusaonethsoantehusaonethusonethusnaotehu")

      // build a huge string
      val sb = new StringBuilder
      for (_ <- 1 to 10000) {
        sb.append("oasnuthoesntihosnteidosentidosentauhsnoetidosentihsoneitdsnuthsin\n")
      }
      testCodec(sb.toString)
    }
  }
}
