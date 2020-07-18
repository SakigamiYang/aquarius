package me.sakigamiyang.aquarius.common.app

import me.sakigamiyang.aquarius.common.logging.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
 * Spark app.
 *
 * @param parameter Either instance, Left is usage, Right is spark parameter
 */
abstract class SparkApp(parameter: Either[String, SparkParameter]) extends Logging with Serializable {
  /**
   * Parameter type.
   */
  type ParameterT <: SparkParameter

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

  @transient lazy val spark: SparkSession = {
    val p = parameter.right.get
    val builder = SparkSession.builder().appName(p.appName)
    if (isLocal) {
      builder.master(p.master)
        .config("spark.driver.host", "localhost")
        .config("spark.sql.shuffle.partitions", "1")
        .config("spark.sql.warehouse.dir", System.getProperty("java.io.tmpdir"))
    }
    if (p.enableHiveSupport) {
      builder.enableHiveSupport()
    }
    builder.getOrCreate()
  }
  @transient lazy val sc: SparkContext = spark.sparkContext
  @transient lazy val sqlc: SQLContext = spark.sqlContext
  @transient lazy val isLocal: Boolean = parameter.right.get.master.startsWith("local")

  /**
   * Run parameter parsing and user task.
   */
  final def apply(): Unit = {
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
      if (spark != null) spark.stop()
    }
  }
}
