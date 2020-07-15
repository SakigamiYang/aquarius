package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging
import org.apache.spark.sql.SparkSession

/**
 * Spark app.
 */
abstract class SparkApp(sparkParameterParser: SparkParameterParser) extends Logging with Serializable {
  /**
   * Parameter type.
   */
  type parameterT <: SparkParameter

  /**
   * Parameter parser type.
   */
  type parameterParserT <: SparkParameterParser

  /**
   * Parser command line options into Parameter.
   *
   * @param args command line options
   * @return instance of Parameter type
   */
  final def parse(args: Array[String]): SparkParameter = sparkParameterParser(args)

  /**
   * Run user task.
   *
   * @param parameters command line parameter
   */
  protected def run(spark: SparkSession, parameters: parameterT): Unit

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
  final def apply(args: Array[String]): Unit = {
    var spark: SparkSession = null
    try {
      val parameter = parse(args).asInstanceOf[parameterT]
      if (parameter != null) {
        val isLocal = parameter.master.startsWith("local")
        spark = {
          val builder = SparkSession.builder().appName(parameter.appName)
          if (isLocal) {
            builder.master(parameter.master)
              .config("spark.driver.host", "localhost")
              .config("spark.sql.shuffle.partitions", "1")
              .config("spark.sql.warehouse.dir", System.getProperty("java.io.tmpdir"))
          }
          if (parameter.enableHiveSupport) {
            builder.enableHiveSupport()
          }
          builder.getOrCreate()
        }

        run(spark, parameter)
      }
    } catch {
      case t: Throwable => onError(t)
    } finally {
      onFinally()
      if (spark != null) spark.stop()
    }
  }
}
