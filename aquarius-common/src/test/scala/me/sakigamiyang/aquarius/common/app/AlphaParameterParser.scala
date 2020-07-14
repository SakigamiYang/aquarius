package me.sakigamiyang.aquarius.common.app

import scopt.OptionParser

final class AlphaParameterParser extends ParameterParser {
  override type parameterT = AlphaParameter

  private lazy val parser = new OptionParser[parameterT]("alpha-app") {
    help("alpha-app")

    opt[String]("name")
      .optional
      .valueName("name")
      .action((value, param) => param.copy(name = value))

    opt[Int]("age")
      .optional
      .valueName("age")
      .action((value, param) => param.copy(age = value))
  }

  def apply(args: Array[String]): parameterT = parser.parse(args, new parameterT) match {
    case Some(param) => param
    case None =>
      parser.showUsageAsError()
      System.exit(1)
      null
  }
}

object AlphaParameterParser
