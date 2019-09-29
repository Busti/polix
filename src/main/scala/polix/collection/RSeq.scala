package polix.collection

import cats.{Foldable, Functor, Monad}
import polix.reactive.Scannable

sealed trait RSeqEvent[A]
case class Insert[A](index: Int, elem: A)                                                       extends RSeqEvent[A]
case class Remove[A](index: Int)                                                                extends RSeqEvent[A]
case class Update[A](index: Int, elem: A)                                                       extends RSeqEvent[A]
case class Combined[A](indexRemoval: Int, indexInsertion: Int, elem: A)                         extends RSeqEvent[A]
case class Patch[A](index: Int, other: IterableOnce[A], replaced: Int)                          extends RSeqEvent[A]
case class MassUpdate[A](indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)]) extends RSeqEvent[A]

abstract class RSeq[A, G[_]: Monad: Scannable] { self =>

  def stream: G[RSeqEvent[A]]

  def materialize: G[Seq[A]] = implicitly[Scannable[G]].scan(self.stream, Seq.empty[A]) {
    case (acc, Insert(i, e))        => acc.take(i) ++ Seq(e) ++ acc.drop(i) // todo: use `list.splitAt`
    case (acc, Remove(i))           => acc.take(i) ++ acc.drop(i + 1)
    case (acc, Update(i, e))        => acc.updated(i, e)
    case (acc, Combined(ir, ii, e)) => (acc.take(ir) ++ acc.drop(ir + 1)).updated(ii, e)
    case (acc, Patch(i, o, r))      => acc.patch(i, o, r)
    case (acc, MassUpdate(ir, in))  =>
      in.iterator.foldLeft(ir.iterator.foldLeft(acc) { case (a, i) => a.take(i) ++ a.drop(i + 1) }) {
        case (a, (i, e)) => a.take(i) ++ Seq(e) ++ a.drop(i)
      }
  }

  def map[B](f: A => B): RSeq[B, G] = new RSeq[B, G] {
    override def stream: G[RSeqEvent[B]] = Functor[G].map(self.stream) {
      case Insert(i, e)        => Insert(i, f(e))
      case Remove(i)           => Remove(i)
      case Update(i, e)        => Update(i, f(e))
      case Combined(ir, ii, e) => Combined(ir, ii, f(e))
      case Patch(i, o, r)      => Patch(i, o.iterator.map(f), r)
      case MassUpdate(ir, in)  => MassUpdate(ir, in.iterator.map { case (i, e) => (i, f(e)) })
    }
  }

  /*def sorted(implicit ord: Ordering[A]): RSeq[A, G] = new RSeq[A, G] {
    override def stream: G[RSeqEvent] = implicitly[Foldable2[G]].foldLeft(self.stream, Seq.empty[A]) {
      case (acc, self.Insert(i, e)) => ???
      case (acc, self.Remove(i)) => ???
      case (acc, self.Update(i, e)) => ???
      case (acc, self.Combined(ir, ii, e)) => ???
      case (acc, self.Patch(i, o, r)) => ???
      case (acc, self.MassUpdate(ir, in)) => ???
    }
  }*/
}
