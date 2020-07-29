package me.sakigamiyang.aquarius.common

package object collection {
  def repeat[T](seq: Seq[T], times: Int): Seq[T] =
    Seq.fill(times)(seq).foldRight(Seq.empty[T])((seq, tempSeq) => seq ++ tempSeq)

  def mergeMapKeepFirst[K, V](one: Map[K, V], another: Map[K, V]): Map[K, V] =
    one ++ another.filterKeys(!one.contains(_))

  def mergeMapKeepLast[K, V](one: Map[K, V], another: Map[K, V]): Map[K, V] =
    one.filterKeys(!another.contains(_)) ++ another
}
