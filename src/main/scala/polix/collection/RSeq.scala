package polix.collection

import cats.{Functor, Monad}
import polix.reactive.Foldable2

abstract class RSeq[A, G[_]: Monad: Foldable2] { self =>
  sealed trait RSeqEvent
  case class Insert(index: Int, elem: A)                                                       extends RSeqEvent
  case class Remove(index: Int)                                                                extends RSeqEvent
  case class Update(index: Int, elem: A)                                                       extends RSeqEvent
  case class Combined(indexRemoval: Int, indexInsertion: Int, elem: A)                         extends RSeqEvent
  case class Patch(index: Int, other: IterableOnce[A], replaced: Int)                          extends RSeqEvent
  case class MassUpdate(indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)]) extends RSeqEvent

  def stream: G[RSeqEvent]

  def materialize: G[Seq[A]] = implicitly[Foldable2[G]].foldLeft(self.stream, Seq.empty[A]) {
    case (acc, self.Insert(i, e))        => acc.take(i) :+ e +: acc.drop(i) // todo: use `list.splitAt`
    case (acc, self.Remove(i))           => acc.take(i) ++ acc.drop(i + 1)
    case (acc, self.Update(i, e))        => acc.updated(i, e)
    case (acc, self.Combined(ir, ii, e)) => (acc.take(ir) ++ acc.drop(ir + 1)).updated(ii, e)
    case (acc, self.Patch(i, o, r))      => acc.patch(i, o, r)
    case (acc, self.MassUpdate(ir, in))  =>
      in.iterator.foldLeft(ir.iterator.foldLeft(acc) { case (a, i) => a.take(i) ++ a.drop(i + 1) }) {
        case (a, (i, e)) => a.take(i) :+ e +: a.drop(i)
      }
  }

  def map[B](f: A => B): RSeq[B, G] = new RSeq[B, G] {
    override def stream: G[RSeqEvent] = Functor[G].map(self.stream) {
      case self.Insert(i, e)        => Insert(i, f(e))
      case self.Remove(i)           => Remove(i)
      case self.Update(i, e)        => Update(i, f(e))
      case self.Combined(ir, ii, e) => Combined(ir, ii, f(e))
      case self.Patch(i, o, r)      => Patch(i, o.iterator.map(f), r)
      case self.MassUpdate(ir, in)  => MassUpdate(ir, in.iterator.map { case (i, e) => (i, f(e)) })
    }
  }

  def sorted(implicit ord: Ordering[A]): RSeq[A, G] = new RSeq[A, G] {
    override def stream: G[RSeqEvent] = implicitly[Foldable2[G]].foldLeft(self.stream, Seq.empty[A]) {
      case (acc, self.Insert(i, e)) => ???
      case (acc, self.Remove(i)) => ???
      case (acc, self.Update(i, e)) => ???
      case (acc, self.Combined(ir, ii, e)) => ???
      case (acc, self.Patch(i, o, r)) => ???
      case (acc, self.MassUpdate(ir, in)) => ???
    }
  }
}
