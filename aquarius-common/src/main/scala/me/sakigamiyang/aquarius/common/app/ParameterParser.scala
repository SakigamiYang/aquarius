package me.sakigamiyang.aquarius.common.app

/**
 * Command line option parser.
 */
abstract class ParameterParser {
  /**
   * Parameter type.
   */
  type parameterT >: Null <: Parameter

  /**
   * Parse command line options into specified Parameter type.
   *
   * @param args command line options
   * @return instance of parameter type
   */
  def apply(args: Array[String]): parameterT
}
