package me.sakigamiyang.aquarius.common.app

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scopt.OptionParser

case class AlphaParameter(override val appName: String = "alpha-app",
                          name: String = "Tom",
                          age: Int = 23)
  extends Parameter(appName)

final class AlphaParameterParser(parameter: Parameter) extends ParameterParser(parameter) {
  override type parameterT = AlphaParameter

  final lazy val parser = new OptionParser[parameterT]("alpha-app") {
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
  }
}

class AppSpec extends AnyFunSpec with Matchers {
  describe("test app") {
    it("use default parameters") {
      val defaultParameter = AlphaParameter()

      final class AlphaApp(parameterParser: ParameterParser) extends App(parameterParser) {
        override type parameterT = AlphaParameter
        override type parameterParserT = AlphaParameterParser

        override def run(parameters: parameterT): Unit = {
          parameters.appName shouldBe defaultParameter.appName
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }
      }

      val args = Array[String]()
      val alphaParameter = AlphaParameter()
      val alphaParameterParser = new AlphaParameterParser(alphaParameter)
      new AlphaApp(alphaParameterParser)(args)
    }

    it("use specific parameters") {
      final class AlphaApp(parameterParser: ParameterParser) extends App(parameterParser) {
        override type parameterT = AlphaParameter
        override type parameterParserT = AlphaParameterParser

        override def run(parameters: parameterT): Unit = {
          parameters.appName shouldBe "customer name"
          parameters.name shouldBe "Jerry"
          parameters.age shouldBe 12
        }
      }

      val args = Array("--name", "Jerry", "--age", "12", "--app-name", "customer name")
      val alphaParameter = AlphaParameter()
      val alphaParameterParser = new AlphaParameterParser(alphaParameter)
      new AlphaApp(alphaParameterParser)(args)
    }

    it("wrong parameters") {
      final class AlphaApp(parameterParser: ParameterParser) extends App(parameterParser) {
        override type parameterT = AlphaParameter
        override type parameterParserT = AlphaParameterParser

        override def run(parameters: parameterT): Unit = {}

        override protected def onError(throwable: Throwable): Unit = {
          throwable.isInstanceOf[CommandLineParseException] shouldBe true
          throwable.asInstanceOf[CommandLineParseException].getMessage.toLowerCase contains "usage:" shouldBe true
        }
      }

      val args = Array("--not-defined", "abc")
      val alphaParameter = AlphaParameter()
      val alphaParameterParser = new AlphaParameterParser(alphaParameter)
      new AlphaApp(alphaParameterParser)(args)
    }

    it("override everything is OK") {
      val defaultParameter = AlphaParameter()

      final class AlphaApp(parameterParser: ParameterParser) extends App(parameterParser) {
        override type parameterT = AlphaParameter
        override type parameterParserT = AlphaParameterParser

        override def run(parameters: parameterT): Unit = {
          parameters.name shouldBe defaultParameter.name
          parameters.age shouldBe defaultParameter.age
        }

        override protected def onError(throwable: Throwable): Unit = "on-error" shouldBe "on-error"

        override protected def onFinally(): Unit = "on-finally" shouldBe "on-finally"
      }

      val args = Array[String]()
      val alphaParameter = AlphaParameter()
      val alphaParameterParser = new AlphaParameterParser(alphaParameter)
      new AlphaApp(alphaParameterParser)(args)
    }
  }
}
