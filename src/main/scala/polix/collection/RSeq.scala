package polix.collection

import cats.Functor
import polix.collection.RSeqMutations._

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

  case class AppendAll[A](elems: IterableOnce[A])                                                 extends RSeqMutation[A]
  case class PrependAll[A](elems: IterableOnce[A])                                                extends RSeqMutation[A]
  case class InsertAll[A](index: Int, elems: IterableOnce[A])                                     extends RSeqMutation[A]
  case class RemoveAll[A](index: Int, count: Int)                                                 extends RSeqMutation[A]
  case class RemoveAllElems[A](elems: IterableOnce[A])                                            extends RSeqMutation[A]
  case class Patch[A](index: Int, other: IterableOnce[A], replaced: Int)                          extends RSeqMutation[A]
  case class MassUpdate[A](indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)]) extends RSeqMutation[A]

}

trait RSeq[A, +G[_]] extends RIterable[A, G] with RSeqOps[A, G, RSeq, RSeq[A, G]] { self =>

  type M = RSeqMutation[A]

  def map[B, J[x] >: G[x] : Functor](f: A => B): RSeq[B, J] = new RSeq[B, J] {
    override def stream: J[RSeqMutation[B]] = Functor[J].map(self.stream) {
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
