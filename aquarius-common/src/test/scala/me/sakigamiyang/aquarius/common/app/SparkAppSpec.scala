package me.sakigamiyang.aquarius.common.app

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scopt.OptionParser

case class AlphaSparkParameter(override val appName: String = "local-spark-job",
                               override val master: String = "local[*]",
                               override val enableHiveSupport: Boolean = false,
                               name: String = "Tom",
                               age: Int = 23) extends SparkParameter

object AlphaSparkParameterParser extends SparkParameterParser {
  override type ParameterT = AlphaSparkParameter

  override protected val parser: OptionParser[ParameterT] = new OptionParser[ParameterT]("alpha-spark-app") {
    help("alpha-spark-app")

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

    override def errorOnUnknownArgument: Boolean = false

    override def reportWarning(msg: String): Unit = {}
  }
}

class SparkAppSpec extends AnyFunSpec with Matchers {
  describe("test spark app") {
    it("use default parameters") {
      val defaultParameter = AlphaSparkParameter()

      final class AlphaSparkApp(parameter: Either[String, SparkParameter]) extends SparkApp(parameter) {
        override type ParameterT = AlphaSparkParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.appName shouldBe defaultParameter.appName
          parameters.master shouldBe defaultParameter.master
          parameters.enableHiveSupport shouldBe defaultParameter.enableHiveSupport
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }
      }

      val args = Array[String]()
      val alphaSparkParameter = AlphaSparkParameterParser(args)(AlphaSparkParameter())
      new AlphaSparkApp(alphaSparkParameter)()
    }

    it("use specific parameters") {
      final class AlphaSparkApp(parameter: Either[String, SparkParameter]) extends SparkApp(parameter) {
        override type ParameterT = AlphaSparkParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.appName shouldBe "customer-app-name"
          parameters.master shouldBe "local[1]"
          parameters.enableHiveSupport shouldBe false
          parameters.name shouldBe "Jerry"
          parameters.age shouldBe 12
        }
      }

      val args = Array(
        "--name", "Jerry",
        "--age", "12",
        "--app-name", "customer-app-name",
        "--master", "local[1]",
        "--enable-hive-support", "false")
      val alphaSparkParameter = AlphaSparkParameterParser(args)(AlphaSparkParameter())
      new AlphaSparkApp(alphaSparkParameter)()
    }

    it("wrong parameters") {
      final class AlphaSparkApp(parameter: Either[String, SparkParameter]) extends SparkApp(parameter) {
        override type ParameterT = AlphaSparkParameter

        override def run(parameters: ParameterT): Unit = {}

        override protected def onError(throwable: Throwable): Unit = {
          throwable.isInstanceOf[CommandLineParseException] shouldBe true
          throwable.asInstanceOf[CommandLineParseException].getMessage.toLowerCase contains "usage:" shouldBe true
        }
      }

      val args = Array("--not-defined", "abc")
      val alphaSparkParameter = AlphaSparkParameterParser(args)(AlphaSparkParameter())
      new AlphaSparkApp(alphaSparkParameter)()
    }

    it("override everything is OK") {
      val defaultParameter = AlphaParameter()

      final class AlphaSparkApp(parameter: Either[String, SparkParameter]) extends SparkApp(parameter) {
        override type ParameterT = AlphaSparkParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }

        override protected def onError(throwable: Throwable): Unit = "on-error" shouldBe "on-error"

        override protected def onFinally(): Unit = "on-finally" shouldBe "on-finally"
      }

      val args = Array[String]()
      val alphaSparkParameter = AlphaSparkParameterParser(args)(AlphaSparkParameter())
      new AlphaSparkApp(alphaSparkParameter)()
    }
  }
}
