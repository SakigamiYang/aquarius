package me.sakigamiyang.aquarius.common.util

import scala.util.{Failure, Success, Try}

/**
 * Retry for function that may fails.
 */
object Retry {
  /**
   * Retry a function several times.
   *
   * @param maxTries How many times to retry.
   * @param delay    Duration (millis) for every time retrying.
   * @param errorFn  Function to run if it fails, to handle the error message.
   * @param fn       The function to run, returning a Try.
   * @tparam T Class tag.
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
