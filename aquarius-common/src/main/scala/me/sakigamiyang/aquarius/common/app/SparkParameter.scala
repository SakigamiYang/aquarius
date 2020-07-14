package me.sakigamiyang.aquarius.common.app

abstract class SparkParameter extends Serializable {
  val appName: String = "local-spark-app"
  val master: String = "local[*]"
  val enableHiveSupport: Boolean = false
}
