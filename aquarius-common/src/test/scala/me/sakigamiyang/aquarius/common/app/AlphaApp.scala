package me.sakigamiyang.aquarius.common.app

final class AlphaApp extends App {
  override type parameterParserT = AlphaParameterParser

  override protected val parameterParser: parameterParserT = new parameterParserT

  override protected def run(parameters: Parameter): Unit = {
    println("run with " + parameters.toString)
  }
}

object AlphaApp {
  def main(args: Array[String]): Unit = {
    new AlphaApp apply args
  }
}
