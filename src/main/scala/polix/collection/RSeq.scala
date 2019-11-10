package polix.collection

import java.util

import scala.language.higherKinds

import cats.Functor
import polix.collection.RSeqMutations._
import polix.collection.internal.operators.{OperatorMap, OperatorSorted}
import polix.reactive.Scannable
import polix.util.SeqUtils._

import scala.collection.IterableOnce

object RSeqMutations {
  sealed trait RSeqMutation[+A]
  case class Append[+A](elem: A)                                           extends RSeqMutation[A]
  case class Prepend[+A](elem: A)                                          extends RSeqMutation[A]
  case class Insert[+A](index: Int, elem: A)                               extends RSeqMutation[A]
  case class Remove[+A](index: Int)                                        extends RSeqMutation[A]
  case class RemoveElem[+A](elem: A)                                       extends RSeqMutation[A]
  case class Update[+A](index: Int, elem: A)                               extends RSeqMutation[A]
  case class Combined[+A](indexRemoval: Int, indexInsertion: Int, elem: A) extends RSeqMutation[A]
  case class AppendAll[+A](elems: IterableOnce[A])                         extends RSeqMutation[A]
  case class PrependAll[+A](elems: IterableOnce[A])                        extends RSeqMutation[A]
  case class InsertAll[+A](index: Int, elems: IterableOnce[A])             extends RSeqMutation[A]
  case class RemoveAll[+A](index: Int, count: Int)                         extends RSeqMutation[A]
  case class RemoveAllElems[+A](elems: IterableOnce[A])                    extends RSeqMutation[A]
  case class Patch[+A](index: Int, other: IterableOnce[A], replaced: Int)  extends RSeqMutation[A]
  case class MassUpdate[+A](indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)])
      extends RSeqMutation[A]
}

trait RSeqOps[+G[_], +A, +CC[_[_], _], +C] extends RIterableOps[G, A, CC, C]

trait RSeq[+G[_], +A] extends RIterable[G, A] with RSeqOps[G, A, RSeq, RSeq[G, A]] {
  self =>
  type M <: RSeqMutation[A]

  def map[G2[x] >: G[x] : Functor, B](f: A => B): RSeq[G2, B] =
    new OperatorMap[G, G2, A, B](self, f)

  def sorted[G2[x] >: G[x] : Scannable, A2 >: A](implicit ord: Ordering[A2]): RSeq[G2, A2] =
    new OperatorSorted[G, G2, A2](self)
}

object RSeq {
  def lift[G[_], A](source: G[RSeqMutation[A]]): RSeq[G, A] = new RSeq[G, A] {
    type M = RSeqMutation[A]

    override def stream: G[RSeqMutation[A]] = source
  }
}
