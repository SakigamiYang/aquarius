package me.sakigamiyang.aquarius.common.app

import scopt.OptionParser

/**
 * Command line option parser.
 */
abstract class ParameterParser extends Serializable {
  /**
   * Parameter type.
   */
  type ParameterT <: Parameter

  /**
   * Parser method.
   */
  protected val parser: OptionParser[ParameterT]

  /**
   * Parse command line options into specific parameter instance.
   *
   * @param args      command line options
   * @param parameter parameter instance
   * @return Either instance, Left is usage, Right is parameter
   */
  def apply(args: Array[String])(parameter: ParameterT): Either[String, ParameterT] =
    parser.parse(args, parameter) match {
      case Some(p) => Right(p)
      case None => Left(parser.usage)
    }
}
