package me.sakigamiyang.aquarius.common.logging

import org.slf4j.{Logger, LoggerFactory}

/**
 * Logging trait.
 */
trait Logging {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(loggerName)

  protected def loggerName: String = getClass.getName
}
