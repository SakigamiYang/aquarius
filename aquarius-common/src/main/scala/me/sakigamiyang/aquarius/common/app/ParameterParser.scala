package me.sakigamiyang.aquarius.common.app

import scopt.OptionParser

/**
 * Command line option parser.
 */
abstract class ParameterParser(parameter: Parameter) extends Serializable {
  /**
   * Parameter type.
   */
  type parameterT <: Parameter

  /**
   * Parameter instance.
   */
  //  protected val parameter: parameterT

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
  def apply(args: Array[String]): parameterT = parser.parse(args, parameter.asInstanceOf[parameterT]) match {
    case Some(param) => param
    case None => throw new CommandLineParseException(parser.usage)
  }
}
