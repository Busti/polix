package polix.collection

import java.util

import cats.Functor
import polix.collection.RSeqMutations._
import polix.reactive.Scannable
import polix.util.SeqUtils._

import scala.collection.IterableOnce
import scala.language.{higherKinds, reflectiveCalls}

object RSeqMutations {
  sealed trait RSeqMutation[A]
  case class Append[A](elem: A)                                           extends RSeqMutation[A]
  case class Prepend[A](elem: A)                                          extends RSeqMutation[A]
  case class Insert[A](index: Int, elem: A)                               extends RSeqMutation[A]
  case class Remove[A](index: Int)                                        extends RSeqMutation[A]
  case class RemoveElem[A](elem: A)                                       extends RSeqMutation[A]
  case class Update[A](index: Int, elem: A)                               extends RSeqMutation[A]
  case class Combined[A](indexRemoval: Int, indexInsertion: Int, elem: A) extends RSeqMutation[A]
  case class AppendAll[A](elems: IterableOnce[A])                         extends RSeqMutation[A]
  case class PrependAll[A](elems: IterableOnce[A])                        extends RSeqMutation[A]
  case class InsertAll[A](index: Int, elems: IterableOnce[A])             extends RSeqMutation[A]
  case class RemoveAll[A](index: Int, count: Int)                         extends RSeqMutation[A]
  case class RemoveAllElems[A](elems: IterableOnce[A])                    extends RSeqMutation[A]
  case class Patch[A](index: Int, other: IterableOnce[A], replaced: Int)  extends RSeqMutation[A]
  case class MassUpdate[A](indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)])
      extends RSeqMutation[A]

}

trait RSeq[A, +G[_]] extends RIterable[A, G] with RSeqOps[A, G, RSeq, RSeq[A, G]] { self =>

  type M = RSeqMutation[A]

  def map[B, G2[x] >: G[x] : Functor](f: A => B): RSeq[B, G2] = new RSeq[B, G2] {
    override def stream: G2[RSeqMutation[B]] = Functor[G2].map(self.stream) {
      case Append(elem)                                 => Append(f(elem))
      case Prepend(elem)                                => Prepend(f(elem))
      case Insert(index, elem)                          => Insert(index, f(elem))
      case Remove(index)                                => Remove(index)
      case RemoveElem(elem)                             => RemoveElem(f(elem))
      case Update(index, elem)                          => Update(index, f(elem))
      case Combined(indexRemoval, indexInsertion, elem) => Combined(indexRemoval, indexInsertion, f(elem))
      case AppendAll(elems)                             => AppendAll(elems.iterator.map(f))
      case PrependAll(elems)                            => PrependAll(elems.iterator.map(f))
      case InsertAll(index, elems)                      => InsertAll(index, elems.iterator.map(f))
      case RemoveAll(index, count)                      => RemoveAll(index, count)
      case RemoveAllElems(elems)                        => RemoveAllElems(elems.iterator.map(f))
      case Patch(index, other, replaced)                => Patch(index, other.iterator.map(f), replaced)
      case MassUpdate(indicesRemoved, insertions) =>
        MassUpdate(indicesRemoved, insertions.iterator.map { case (i, e) => (i, f(e)) })
    }
  }

  def sorted[A2 >: A, G2[x] >: G[x] : Scannable](implicit ord: Ordering[A]): RSeq[A2, G2] = new RSeq[A2, G2] {
    case class Repr(src: Seq[A], dst: Seq[A])

    override def stream: G2[RSeqMutation[A2]] =
      Scannable[G2].scanAccumulate(self.stream, Repr(Vector.empty, Vector.empty)) { (acc, mut) =>
        def insertion(index: Int, elem: A): (Repr, RSeqMutation[A2]) = {
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
            val src = acc.src.remove(indexRemoval).insert(indexInsertion, elem)
            val prevDstRemIndex = acc.dst.indexOf(prevRemElem)
            val (dst, idx) = acc.dst.remove(prevDstRemIndex).sortedInsert(elem)
            (Repr(src, dst), Combined(prevDstRemIndex, idx, elem))
        }
      }
  }
}

trait RSeqOps[+A, +G[_], +CC[_, _[_]], +C] extends RIterableOps[A, G, CC, C]
