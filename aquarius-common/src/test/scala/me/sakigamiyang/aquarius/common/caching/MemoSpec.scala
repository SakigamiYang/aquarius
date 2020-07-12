package me.sakigamiyang.aquarius.common.caching

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ArrayBuffer

class MemoSpec extends AnyFunSpec with Matchers {
  describe("test memoize") {
    it("memoize function results") {
      val calculatedValues = new ArrayBuffer[String]
      val plusOne = Memo.memoize { (x: Int) => {
        val result = (x + 1).toString

        // Counting the number of calculated values.
        // Cached value shall not be calculated once more time.
        calculatedValues += result
        result
      }
      }

      plusOne(1) should be("2")
      plusOne(2) should be("3")
      plusOne(1) should be("2")

      calculatedValues.size should be(2)
    }

    it("memoize function results with multiple parameters") {
      val calculatedValues = new ArrayBuffer[String]
      val plusOne: ((Int, Int)) => String = Memo.memoize {
        case (x: Int, y: Int) =>
          val result1 = x + 1
          val result2 = y + 1
          val result = s"($result1, $result2)"

          // Counting the number of calculated values.
          // Cached value shall not be calculated once more time.
          calculatedValues += result
          result
      }

      plusOne(1, 2) should be("(2, 3)")
      plusOne(2, 3) should be("(3, 4)")
      plusOne(1, 2) should be("(2, 3)")

      calculatedValues.size should be(2)
    }
  }
}
