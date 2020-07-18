package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging

/**
 * App.
 *
 * @param parameter Either instance, Left is usage, Right is parameter
 */
abstract class App(parameter: Either[String, Parameter]) extends Logging with Serializable {
  /**
   * Parameter type.
   */
  type ParameterT <: Parameter

  /**
   * Run user task.
   *
   * @param parameter command line parameter
   */
  protected def run(parameter: ParameterT): Unit

  /**
   * On command line parsing error.
   *
   * @param clpe exception
   */
  protected def onCommandLineParsingError(clpe: CommandLineParseException): Unit = clpe.printStackTrace(System.err)

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
   */
  final def apply(): Unit =
    try {
      parameter match {
        case Right(param) => run(param.asInstanceOf[ParameterT])
        case Left(usage) => throw new CommandLineParseException(usage)
      }
    } catch {
      case clpe: CommandLineParseException => onCommandLineParsingError(clpe)
      case t: Throwable => onError(t)
    } finally {
      onFinally()
    }

}
