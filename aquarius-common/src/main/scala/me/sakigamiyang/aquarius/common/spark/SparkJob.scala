package me.sakigamiyang.aquarius.common.spark

import me.sakigamiyang.aquarius.common.logging.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
 * Base class of Spark jobs.
 *
 * @param appName           SparkSession.Builder.appName.
 * @param master            SparkSession.Builder.master.
 * @param enableHiveSupport SparkSession.Builder.enableHiveSupport.
 */
abstract class SparkJob(val appName: String = "local-spark-job",
                        val master: String = "local[*]",
                        val enableHiveSupport: Boolean = false) extends Logging with Serializable {
  @transient lazy implicit val spark: SparkSession = {
    val builder = SparkSession.builder().appName(appName)

    if (isLocal) {
      builder.master(master)
        .config("spark.driver.host", "localhost")
        .config("spark.sql.shuffle.partitions", "1")
        .config("spark.sql.warehouse.dir", System.getProperty("java.io.tmpdir"))
    }
    if (enableHiveSupport) {
      builder.enableHiveSupport()
    }
    builder.getOrCreate()
  }

  @transient lazy val sc: SparkContext = spark.sparkContext
  @transient lazy val sqlc: SQLContext = spark.sqlContext

  lazy val isLocal: Boolean = master.startsWith("local")

  def run(): Unit

  def stop(): Unit = {
    if (!isLocal) println(s"spark_application_id_for_yarn_log=${sc.applicationId}")
    spark.stop()
  }
}
