package polix.collection

import java.util

import cats.Functor
import polix.collection.RSeqMutations._
import polix.collection.internal.operators.{OperatorMap, OperatorSorted}
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

  def map[B, G2[x] >: G[x] : Functor](f: A => B): RSeq[B, G2] =
    new OperatorMap[A, B, G, G2](self, f)

  def sorted[A2 >: A, G2[x] >: G[x] : Scannable](implicit ord: Ordering[A2]): RSeq[A2, G2] =
    new OperatorSorted[A, A2, G, G2](self)
}

trait RSeqOps[+A, +G[_], +CC[_, _[_]], +C] extends RIterableOps[A, G, CC, C]
