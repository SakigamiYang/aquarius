package me.sakigamiyang.aquarius.common.collection

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PackageSpec extends AnyFunSpec with Matchers {
  describe("test collection") {
    it("repeat the same seq n times and merge into one") {
      repeat(Seq(1, 2, 3), 1) shouldBe Seq(1, 2, 3)
      repeat(Seq(1, 2, 3), 4) shouldBe Seq(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3)
    }

    it("merge 2 maps and keep first for the same key") {
      val mapA = Map("a" -> 1)
      val mapB = Map("b" -> 2, "c" -> 3)
      val mapC = Map("a" -> 100, "d" -> 4, "e" -> 5)

      mergeMapKeepFirst(mapA, mapB) shouldBe Map("a" -> 1, "b" -> 2, "c" -> 3)
      mergeMapKeepFirst(mapA, mapC) shouldBe Map("a" -> 1, "d" -> 4, "e" -> 5)
    }

    it("merge 2 maps and keep last for the same key") {
      val mapA = Map("a" -> 1)
      val mapB = Map("b" -> 2, "c" -> 3)
      val mapC = Map("a" -> 100, "d" -> 4, "e" -> 5)

      mergeMapKeepLast(mapA, mapB) shouldBe Map("a" -> 1, "b" -> 2, "c" -> 3)
      mergeMapKeepLast(mapA, mapC) shouldBe Map("a" -> 100, "d" -> 4, "e" -> 5)
    }
  }
}
