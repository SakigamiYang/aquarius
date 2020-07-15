package me.sakigamiyang.aquarius.common.app

import scopt.OptionParser

/**
 * Command line option parser.
 */
abstract class SparkParameterParser(sparkParameter: SparkParameter) extends Serializable {
  /**
   * Parameter type.
   */
  type parameterT <: SparkParameter

  /**
   * Parser method.
   */
  protected val parser: OptionParser[parameterT]

  /**
   * Parse command line options into specified Parameter type.
   *
   * @param args command line options
   * @return instance of parameter type
   */
  def apply(args: Array[String]): parameterT = parser.parse(args, sparkParameter.asInstanceOf[parameterT]) match {
    case Some(param) => param
    case None => throw new CommandLineParseException(parser.usage)
  }
}
