package polix.collection

import cats.Functor
import polix.collection.RSeqMutations._

import scala.collection.IterableOnce
import scala.language.{higherKinds, reflectiveCalls}

object RSeqMutations {
  sealed trait RSeqMutation[A]
  case class Append[A](elem: A)                    extends RSeqMutation[A]
  case class Prepend[A](elem: A)                   extends RSeqMutation[A]
  case class Insert[A](index: Int, elem: A)        extends RSeqMutation[A]
  case class Update[A](index: Int, elem: A)        extends RSeqMutation[A]
  case class Move[A](fromIndex: Int, toIndex: Int) extends RSeqMutation[A]
  case class RemoveIndex[A](index: Int)            extends RSeqMutation[A]
  case class RemoveElem[A](elem: A)                extends RSeqMutation[A]
}

trait RSeq[A, +G[_]] extends RIterable[A, G] with RSeqOps[A, G, RSeq, RSeq[A, G]] { self =>
  type M = RSeqMutation[A]

  override def map[B, G2[x] >: G : Functor](f: A => B): RSeq[B, G2] = new RSeq[B, G2] {
    override def stream: G2[IterableOnce[RSeqMutation[B]]] =
      Functor[G2].map(self.stream)(_.iterator.map {
        case Append(elem)             => Append(f(elem))
        case Prepend(elem)            => Prepend(f(elem))
        case Insert(index, elem)      => Insert(index, f(elem))
        case Update(index, elem)      => Update(index, f(elem))
        case Move(fromIndex, toIndex) => Move(fromIndex, toIndex)
        case RemoveIndex(index)       => RemoveIndex(index)
        case RemoveElem(elem)         => RemoveElem(f(elem))
      })
  }
}

trait RSeqOps[+A, +G[_], +CC[_, _[_]], +C] extends RIterableOps[A, G, CC, C]

//abstract class RSeq[A, G[_]: Monad: Scannable] { self =>
//
//  def stream: G[RSeqEvent[A]]
//
//  def materialize: G[Seq[A]] = implicitly[Scannable[G]].scan(self.stream, Seq.empty[A]) {
//    case (acc, Insert(i, e))        => acc.take(i) ++ Seq(e) ++ acc.drop(i) // todo: use `list.splitAt`
//    case (acc, Remove(i))           => acc.take(i) ++ acc.drop(i + 1)
//    case (acc, Update(i, e))        => acc.updated(i, e)
//    case (acc, Combined(ir, ii, e)) => (acc.take(ir) ++ acc.drop(ir + 1)).updated(ii, e)
//    case (acc, Patch(i, o, r))      => acc.patch(i, o, r)
//    case (acc, MassUpdate(ir, in))  =>
//      in.iterator.foldLeft(ir.iterator.foldLeft(acc) { case (a, i) => a.take(i) ++ a.drop(i + 1) }) {
//        case (a, (i, e)) => a.take(i) ++ Seq(e) ++ a.drop(i)
//      }
//  }
//
//  /*def sorted(implicit ord: Ordering[A]): RSeq[A, G] = new RSeq[A, G] {
//    override def stream: G[RSeqEvent] = implicitly[Foldable2[G]].foldLeft(self.stream, Seq.empty[A]) {
//      case (acc, Insert(i, e)) => ???
//      case (acc, Remove(i)) => ???
//      case (acc, Update(i, e)) => ???
//      case (acc, Combined(ir, ii, e)) => ???
//      case (acc, Patch(i, o, r)) => ???
//      case (acc, MassUpdate(ir, in)) => ???
//    }
//  }*/
//}
//
//object RSeq {
//  def from[A, G[_]: Monad: Scannable](initialStream: G[RSeqEvent[A]]): RSeq[A, G] = new RSeq[A, G] {
//    override def stream: G[RSeqEvent[A]] = initialStream
//  }
//}
