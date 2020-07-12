package me.sakigamiyang.aquarius.common.caching

import scala.collection.mutable

/**
 * Memoize
 */
object Memo {
  /**
   * Memoize results for idempotent functions.
   * For a thread safe implementation see [[Memo.threadSafeMemoize]].
   *
   * @param fn function
   * @tparam A input type
   * @tparam B output type
   * @return calculated, or memoized result if it has already been calculated
   */
  def memoize[A, B](fn: A => B): A => B = new mutable.HashMap[A, B]() {
    self =>
    override def apply(key: A): B = self.getOrElseUpdate(key, fn(key))
  }

  /**
   * Memoize results for idempotent functions with a thread safe structure
   *
   * @param fn function
   * @tparam A input type
   * @tparam B output type
   * @return calculated, or memoized result if it has already been calculated
   */
  def threadSafeMemoize[A, B](fn: A => B): A => B = new mutable.HashMap[A, B]() {
    self =>
    override def apply(key: A): B = self.synchronized(getOrElseUpdate(key, fn(key)))
  }
}
