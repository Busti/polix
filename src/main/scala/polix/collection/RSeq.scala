package polix.collection

import cats.Functor

import scala.language.{higherKinds, reflectiveCalls}

trait RSeq[A, +G[_]] extends RIterable[A, G] with RSeqOps[A, G, RSeq, RSeq[A, G]] { self =>
  sealed trait RSeqEvent
  case class Insert(index: Int, elem: A)                                                       extends RSeqEvent
  case class Remove(index: Int)                                                                extends RSeqEvent
  case class Update(index: Int, elem: A)                                                       extends RSeqEvent
  case class Combined(indexRemoval: Int, indexInsertion: Int, elem: A)                         extends RSeqEvent
  case class Patch(index: Int, other: IterableOnce[A], replaced: Int)                          extends RSeqEvent
  case class MassUpdate(indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)]) extends RSeqEvent

  type E = RSeqEvent

  def map[B, J[x] >: G[x] : Functor](f: A => B): RSeq[B, J] = new RSeq[B, J] {
    override def stream: J[RSeqEvent] = Functor[J].map(self.stream) {
      case self.Insert(index, elem)                          => Insert(index, f(elem))
      case self.Remove(index)                                => Remove(index)
      case self.Update(index, elem)                          => Update(index, f(elem))
      case self.Combined(indexRemoval, indexInsertion, elem) => Combined(indexRemoval, indexInsertion, f(elem))
      case self.Patch(index, other, replaced)                => Patch(index, other.iterator.map(f), replaced)
      case self.MassUpdate(indicesRemoved, insertions) =>
        MassUpdate(indicesRemoved, insertions.iterator.map { case (i, e) => (i, f(e)) })
    }
  }
}

trait RSeqOps[+A, +G[_], +CC[_, _[_]], +C] extends RIterableOps[A, G, CC, C]
