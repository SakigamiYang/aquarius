package me.sakigami_yang.aquarius.common.spark

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

abstract class SparkJob(val jobName: String = "local-spark-job",
                        val master: String = "local[*]",
                        val enableHiveSupport: Boolean = false) extends Logging with Serializable {
  @transient lazy implicit val spark: SparkSession = {
    val builder = SparkSession.builder().appName(jobName)

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

  import spark.implicits._

  lazy val isLocal: Boolean = master.startsWith("local")

  def run(): Unit

  def stop(): Unit = {
    if (!isLocal) println(s"spark_application_id_for_yarn_log=${sc.applicationId}")
    spark.stop()
  }
}
