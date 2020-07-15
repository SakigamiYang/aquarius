package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging
import org.apache.spark.sql.SparkSession

/**
 * Spark app.
 */
abstract class SparkApp extends Logging with Serializable {
  /**
   * Parameter type.
   */
  type parameterT <: SparkParameter

  /**
   * Parameter parser type.
   */
  type parameterParserT <: SparkParameterParser

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
  final def parse(args: Array[String]): SparkParameter = parameterParser(args)

  /**
   * Run user task.
   *
   * @param parameters command line parameter
   */
  protected def run(spark: SparkSession, isLocal: Boolean, parameters: parameterT): Unit

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
      val parameters = parse(args).asInstanceOf[parameterT]
      if (parameters != null) {
        val isLocal = parameters.master.startsWith("local")
        spark = {
          val builder = SparkSession.builder().appName(parameters.appName)
          if (isLocal) {
            builder.master(parameters.master)
              .config("spark.driver.host", "localhost")
              .config("spark.sql.shuffle.partitions", "1")
              .config("spark.sql.warehouse.dir", System.getProperty("java.io.tmpdir"))
          }
          if (parameters.enableHiveSupport) {
            builder.enableHiveSupport()
          }
          builder.getOrCreate()
        }

        run(spark, isLocal, parameters)
      }
    } catch {
      case t: Throwable => onError(t)
    } finally {
      onFinally()
      if (spark != null) spark.stop()
    }
  }
}
