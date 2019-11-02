package polix.util

object SortedSeqUtils {
  implicit class SeqOps[T](val wrapped: Seq[T]) extends AnyVal {
    def sortedInsert(elem: T)(implicit ord2: Ordering[T]): (Seq[T], Int) = {
      val pos = wrapped.indexWhere(elem => ord2.gt(elem, elem))
      val dst = wrapped.patch(pos, Iterable.single(elem), 0)
      (dst, pos)
    }
  }
}