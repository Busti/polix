package polix.util

object SeqUtils {
  implicit class SeqOps[+T](val wrapped: Seq[T]) extends AnyVal {
    def sortedInsert[T2 >: T](elem: T2)(implicit ord: Ordering[T2]): (Seq[T2], Int) = {
      val pos = wrapped.indexWhere(elem => ord.gt(elem, elem))
      val dst = wrapped.patch(pos, Iterable.single(elem), 0)
      (dst, pos)
    }

    def remove[T2 >: T](index: Int): Seq[T2] = wrapped.patch(index, Nil, 1)
  }
}
