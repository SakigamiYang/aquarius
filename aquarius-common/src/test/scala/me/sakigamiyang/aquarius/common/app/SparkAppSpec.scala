package me.sakigamiyang.aquarius.common.app

import org.apache.spark.sql.SparkSession
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scopt.OptionParser

final case class AlphaSparkParameter(override val appName: String = "local-spark-job",
                                     override val master: String = "local[*]",
                                     override val enableHiveSupport: Boolean = false,
                                     name: String = "Tom",
                                     age: Int = 23) extends SparkParameter

final class AlphaSparkParameterParser extends SparkParameterParser {
  override type parameterT = AlphaSparkParameter

  override val parameter: parameterT = AlphaSparkParameter()

  final lazy val parser = new OptionParser[parameterT]("alpha-app") {
    help("alpha-app")

    opt[String]("app-name")
      .optional
      .valueName("app-name")
      .action((value, param) => param.copy(appName = value))

    opt[String]("master")
      .optional
      .valueName("master")
      .action((value, param) => param.copy(master = value))

    opt[Boolean]("enable-hive-support")
      .optional
      .valueName("app-name")
      .action((value, param) => param.copy(enableHiveSupport = value))

    opt[String]("name")
      .optional
      .valueName("name")
      .action((value, param) => param.copy(name = value))

    opt[Int]("age")
      .optional
      .valueName("age")
      .action((value, param) => param.copy(age = value))

    override def showUsageOnError: Boolean = false
  }
}

class SparkAppSpec extends AnyFunSpec with Matchers {
  describe("test spark app") {
    it("use default parameters") {
      val defaultParameter = AlphaSparkParameter()

      final class AlphaSparkApp extends SparkApp {
        override type parameterT = AlphaSparkParameter
        override type parameterParserT = AlphaSparkParameterParser

        override val parameterParser: parameterParserT = new parameterParserT

        override def run(spark: SparkSession, isLocal: Boolean, parameters: parameterT): Unit = {
          parameters.appName shouldBe defaultParameter.appName
          parameters.master shouldBe defaultParameter.master
          parameters.enableHiveSupport shouldBe defaultParameter.enableHiveSupport
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }
      }

      new AlphaSparkApp apply Array()
    }

    it("use specific parameters") {
      final class AlphaSparkApp extends SparkApp {
        override type parameterT = AlphaSparkParameter
        override type parameterParserT = AlphaSparkParameterParser

        override val parameterParser: parameterParserT = new parameterParserT

        override def run(spark: SparkSession, isLocal: Boolean, parameters: parameterT): Unit = {
          parameters.appName shouldBe "customer-app-name"
          parameters.master shouldBe "local[1]"
          parameters.enableHiveSupport shouldBe false
          parameters.name shouldBe "Jerry"
          parameters.age shouldBe 12
        }
      }

      new AlphaSparkApp apply Array("--name", "Jerry",
        "--age", "12",
        "--app-name", "customer-app-name",
        "--master", "local[1]",
        "--enable-hive-support", "false")
    }

    it("wrong parameters") {
      final class AlphaSparkApp extends SparkApp {
        override type parameterT = AlphaSparkParameter
        override type parameterParserT = AlphaSparkParameterParser

        override val parameterParser: parameterParserT = new parameterParserT

        override def run(spark: SparkSession, isLocal: Boolean, parameters: parameterT): Unit = {}

        override protected def onError(throwable: Throwable): Unit = {
          throwable.isInstanceOf[CommandLineParseException] shouldBe true
          throwable.asInstanceOf[CommandLineParseException].getMessage.toLowerCase contains "usage:" shouldBe true
        }
      }

      new AlphaSparkApp apply Array("--not-defined", "abc")
    }

    it("override everything is OK") {
      val defaultParameter = AlphaParameter()

      final class AlphaSparkApp extends SparkApp {
        override type parameterT = AlphaSparkParameter
        override type parameterParserT = AlphaSparkParameterParser

        override val parameterParser: parameterParserT = new parameterParserT

        override def run(spark: SparkSession, isLocal: Boolean, parameters: parameterT): Unit = {
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }

        override protected def onError(throwable: Throwable): Unit = "on-error" shouldBe "on-error"

        override protected def onFinally(): Unit = "on-finally" shouldBe "on-finally"
      }

      new AlphaSparkApp apply Array()
    }
  }
}
