package me.sakigami_yang.aquarius.common.spark

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(logName)

  protected def logName: String = this.getClass.getName.stripSuffix("$")
}
