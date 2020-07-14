package me.sakigamiyang.aquarius.common.util

import scala.util.{Failure, Success, Try}

/**
 * Retry for function that may fails.
 */
object Retry {
  /**
   * Retry a function several times.
   *
   * @param maxTries how many times to retry.
   * @param delay    duration (millis) for every time retrying.
   * @param errorFn  function to run if it fails, to handle the error message.
   * @param fn       the function to run, returning a Try.
   * @tparam T class tag.
   * @return Try[Result] of the function.
   */
  def apply[T](maxTries: Int,
               delay: Long = 0,
               errorFn: String => Unit = _ => Unit)(fn: => Try[T]): Try[T] = {
    @scala.annotation.tailrec
    def retry(remainingTries: Int, delay: Long, errorFn: String => Unit)(fn: => Try[T]): Try[T] = {
      fn match {
        case Success(success) => Success(success)
        case _ if remainingTries > 1 =>
          Thread.sleep(delay)
          retry(remainingTries - 1, delay, errorFn)(fn)
        case Failure(failure) =>
          errorFn(s"Tried $maxTries times, still not enough: ${failure.getMessage}")
          Failure(failure)
      }
    }

    retry(maxTries, delay, errorFn)(fn)
  }
}
