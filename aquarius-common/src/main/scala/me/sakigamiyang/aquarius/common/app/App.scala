package me.sakigamiyang.aquarius.common.app

/**
 * App
 */
abstract class App {
  /**
   * Parameter parser type.
   */
  type parameterParserT <: ParameterParser

  /**
   * Parameter parser.
   */
  protected val parameterParser: parameterParserT

  /**
   * Parser command line options into Parameter.
   *
   * @param args command line options
   * @return instance of Parameter type
   */
  final def parse(args: Array[String]): Parameter = {
    parameterParser(args)
  }

  /**
   * Run user task.
   *
   * @param parameters command line parameter
   */
  protected def run(parameters: Parameter): Unit = {}

  /**
   * On error.
   *
   * @param throwable exception
   */
  protected def onErrorExit1(throwable: Throwable): Unit = {
    throwable.printStackTrace(System.err)
  }

  /**
   * On finally.
   */
  protected def onFinally(): Unit = {}

  /**
   * Run parameter parsing and user task.
   *
   * @param args command line options
   */
  final def apply(args: Array[String]): Unit = {
    try {
      val parameters = parse(args)
      run(parameters)
    } catch {
      case t: Throwable =>
        onErrorExit1(t)
        System.exit(1)
    } finally {
      onFinally()
    }
  }

}
