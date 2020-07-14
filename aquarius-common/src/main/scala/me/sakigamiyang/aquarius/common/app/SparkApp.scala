package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

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
   * Parameter.
   */
  protected var parameters: parameterT = _

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

  @transient lazy val spark: SparkSession =
    if (parameters == null) null
    else {
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
  @transient lazy val sc: SparkContext = spark.sparkContext
  @transient lazy val sqlc: SQLContext = spark.sqlContext
  @transient lazy val isLocal: Boolean = parameters.master.startsWith("local")

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
  final def apply(args: Array[String]): Unit = {
    try {
      parameters = parse(args).asInstanceOf[parameterT]
      if (parameters != null) run(parameters)
    } catch {
      case t: Throwable => onError(t)
    } finally {
      onFinally()
      if (spark != null) spark.stop()
    }
  }
}
