package me.sakigamiyang.aquarius.common.app

import scopt.OptionParser

/**
 * Command line option parser.
 */
abstract class SparkParameterParser extends Serializable {
  /**
   * Parameter type.
   */
  type ParameterT <: SparkParameter

  /**
   * Parser method.
   */
  protected val parser: OptionParser[ParameterT]

  /**
   * Parse command line options into specific parameter instance.
   *
   * @param args           command line options
   * @param sparkParameter parameter instance
   * @return Either instance, Left is usage, Right is spark parameter
   */
  def apply(args: Array[String])(sparkParameter: ParameterT): Either[String, ParameterT] =
    parser.parse(args, sparkParameter) match {
      case Some(p) => Right(p)
      case None => Left(parser.usage)
    }
}
