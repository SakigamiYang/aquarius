package me.sakigami_yang.aquarius.common.spark

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.functions._

class SparkJobSpec extends AnyFunSpec with Matchers {
  describe("test configurations for SparkJob") {
    it("config is default") {
      class AJob extends SparkJob {
        override def run(): Unit = {
          // do nothing
        }
      }

      val ajob = new AJob
      ajob.appName shouldBe "local-spark-job"
      ajob.master shouldBe "local[*]"
      ajob.isLocal shouldBe true
      ajob.run()
      ajob.stop()
    }

    it("config is customized") {
      class BJob extends SparkJob {
        override val appName: String = "b-job"
        override val master: String = "local[1]"

        override def run(): Unit = {
          import spark.implicits._
          val df = spark.createDataFrame(Seq((1, "1"), (2, "2"), (3, "c"))).toDF("col1", "col2")
          df.select("col1").agg(sum("col1")).collect()(0).getLong(0) shouldBe 6L
        }
      }

      val bjob = new BJob
      bjob.appName shouldBe "b-job"
      bjob.master shouldBe "local[1]"
      bjob.isLocal shouldBe true
      bjob.run()
      bjob.stop()
    }
  }
}
