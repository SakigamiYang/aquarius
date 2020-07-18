package me.sakigamiyang.aquarius.common.app

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scopt.OptionParser

case class AlphaParameter(override val appName: String = "alpha-app",
                          name: String = "Tom",
                          age: Int = 23)
  extends Parameter(appName)

object AlphaParameterParser extends ParameterParser {
  override type ParameterT = AlphaParameter

  override protected val parser: OptionParser[ParameterT] =
    new OptionParser[ParameterT]("alpha-app") {
      help("alpha-app")

      opt[String]("app-name")
        .optional
        .valueName("app-name")
        .action((value, param) => param.copy(appName = value))

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

class AppSpec extends AnyFunSpec with Matchers {
  describe("test app") {
    it("use default parameters") {
      val defaultParameter = AlphaParameter()

      final class AlphaApp(parameter: Either[String, Parameter]) extends App(parameter) {
        override type ParameterT = AlphaParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.appName shouldBe defaultParameter.appName
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }
      }

      val args = Array[String]()
      val alphaParameter = AlphaParameterParser(args)(AlphaParameter())
      new AlphaApp(alphaParameter)()
    }

    it("use specific parameters") {
      final class AlphaApp(parameter: Either[String, Parameter]) extends App(parameter) {
        override type ParameterT = AlphaParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.appName shouldBe "customer name"
          parameters.name shouldBe "Jerry"
          parameters.age shouldBe 12
        }
      }

      val args = Array("--name", "Jerry", "--age", "12", "--app-name", "customer name")
      val alphaParameter = AlphaParameterParser(args)(AlphaParameter())
      new AlphaApp(alphaParameter)()
    }

    it("wrong parameters") {
      final class AlphaApp(parameter: Either[String, Parameter]) extends App(parameter) {
        override type ParameterT = AlphaParameter

        override def run(parameters: ParameterT): Unit = {}

        override protected def onCommandLineParsingError(clpe: CommandLineParseException): Unit = {
          clpe.getMessage.toLowerCase.contains("usage:") shouldBe true
        }
      }

      val args = Array("--not-defined", "abc")
      val alphaParameter = AlphaParameterParser(args)(AlphaParameter())
      new AlphaApp(alphaParameter)()
    }

    it("override everything is OK") {
      val defaultParameter = AlphaParameter()

      final class AlphaApp(parameter: Either[String, Parameter]) extends App(parameter) {
        override type ParameterT = AlphaParameter

        override def run(parameters: ParameterT): Unit = {
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }

        override protected def onError(throwable: Throwable): Unit = "on-error" shouldBe "on-error"

        override protected def onFinally(): Unit = "on-finally" shouldBe "on-finally"
      }

      val args = Array[String]()
      val alphaParameter = AlphaParameterParser(args)(AlphaParameter())
      new AlphaApp(alphaParameter)()
    }
  }
}
