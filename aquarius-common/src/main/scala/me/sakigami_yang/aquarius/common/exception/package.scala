package me.sakigami_yang.aquarius.common

import java.io.{PrintWriter, StringWriter}

package object exception {
  /**
   * Transforms StackTraces into a String using StringWriter.
   *
   * @param throwable Throwable exception.
   * @return Content of StackTrace.
   */
  def stackTraceToString(throwable: Throwable): String = {
    val w = new StringWriter()
    throwable.printStackTrace(new PrintWriter(w))
    w.toString
  }
}
