package polix.collection.internal.operators

import scala.language.higherKinds

import polix.collection.RSeq
import polix.collection.RSeqMutations._
import polix.reactive.Scannable
import polix.util.SeqUtils._

class OperatorSorted[G[_], G2[x] >: G[x] : Scannable, A](
  source: RSeq[G, A]
)(
  implicit ord: Ordering[A]
) extends RSeq[G2, A] {
  type M = RSeqMutation[A]

  case class Repr(src: Seq[A], dst: Seq[A])

  override def stream: G2[RSeqMutation[A]] =
    Scannable[G2].scanAccumulate(source.stream, Repr(Vector.empty, Vector.empty)) { (acc, mut) =>
      def insertion(index: Int, elem: A): (Repr, RSeqMutation[A]) = {
        val (src, idx) = acc.dst.sortedInsert(elem) // todo: use tree based impl
        val dst        = acc.src.patch(index, Iterable.single(elem), 0)
        (Repr(src, dst), Insert(idx, elem))
      }

      mut match {
        case Append(elem)        => insertion(0, elem)
        case Prepend(elem)       => insertion(acc.src.length, elem)
        case Insert(index, elem) => insertion(index, elem)
        case Remove(index) =>
          val elem     = acc.src(index) // todo: do these in one operation
          val src      = acc.src.remove(index)
          val dstIndex = acc.dst.indexOf(elem)
          val dst      = acc.dst.remove(dstIndex)
          (Repr(src, dst), Remove(dstIndex))
        case RemoveElem(elem) =>
          (Repr(acc.src.filter(_ == elem), acc.dst.filter(_ == elem)), RemoveElem(elem))
        case Update(index, elem) =>
          val prevElem     = acc.src(index)
          val prevDstIndex = acc.dst.indexOf(prevElem)
          val (dst, idx) = acc.dst
            .remove(prevDstIndex)
            .sortedInsert(elem)
          (Repr(acc.src.updated(index, elem), dst), Combined(prevDstIndex, idx, elem))
        case Combined(indexRemoval, indexInsertion, elem) =>
          val prevRemElem     = acc.src(indexRemoval)
          val src             = acc.src.remove(indexRemoval).insert(indexInsertion, elem)
          val prevDstRemIndex = acc.dst.indexOf(prevRemElem)
          val (dst, idx)      = acc.dst.remove(prevDstRemIndex).sortedInsert(elem)
          (Repr(src, dst), Combined(prevDstRemIndex, idx, elem))
      }
    }
}
