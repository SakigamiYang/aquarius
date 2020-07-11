package me.sakigami_yang.aquarius.common.logging

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(logName)

  protected def logName: String = this.getClass.getName.stripSuffix("$")

  def logDebug(msg: String): Unit = if (logger.isDebugEnabled) logger.debug(msg)

  def logDebug(msg: String, t: Throwable): Unit = if (logger.isDebugEnabled) logger.debug(msg, t)

  def logError(msg: String): Unit = if (logger.isErrorEnabled) logger.error(msg)

  def logError(msg: String, t: Throwable): Unit = if (logger.isErrorEnabled) logger.error(msg, t)

  def logInfo(msg: String): Unit = if (logger.isInfoEnabled) logger.info(msg)

  def logInfo(msg: String, t: Throwable): Unit = if (logger.isInfoEnabled) logger.info(msg, t)

  def logTrace(msg: String): Unit = if (logger.isTraceEnabled) logger.trace(msg)

  def logTrace(msg: String, t: Throwable): Unit = if (logger.isTraceEnabled) logger.trace(msg, t)

  def logWarn(msg: String): Unit = if (logger.isWarnEnabled) logger.warn(msg)

  def logWarn(msg: String, t: Throwable): Unit = if (logger.isWarnEnabled) logger.warn(msg, t)
}
