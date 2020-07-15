package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging

/**
 * App
 */
abstract class App(parameterParser: ParameterParser) extends Logging with Serializable {
  /**
   * Parameter type.
   */
  type parameterT <: Parameter

  /**
   * Parameter parser type.
   */
  type parameterParserT <: ParameterParser

  /**
   * Parameter parser.
   */
  //  protected val parameterParser: parameterParserT

  /**
   * Parser command line options into Parameter.
   *
   * @param args command line options
   * @return instance of Parameter type
   */
  final def parse(args: Array[String]): Parameter = parameterParser.asInstanceOf[parameterParserT](args)

  /**
   * Run user task.
   *
   * @param parameters command line parameter
   */
  protected def run(parameters: parameterT): Unit

  /**
   * On error.
   *
   * @param throwable exception
   */
  protected def onError(throwable: Throwable): Unit = throwable.printStackTrace(System.err)

  /**
   * On finally.
   */
  protected def onFinally(): Unit = {}

  /**
   * Run parameter parsing and user task.
   *
   * @param args command line options
   */
  final def apply(args: Array[String]): Unit =
    try {
      val parameters = parse(args).asInstanceOf[parameterT]
      if (parameters != null) run(parameters)
    } catch {
      case t: Throwable => onError(t)
    } finally {
      onFinally()
    }

}
