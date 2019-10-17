package polix.collection

import cats.Functor

import scala.language.{higherKinds, reflectiveCalls}

trait RSeq[A, +G[_]] extends RIterable[A, G] with RSeqOps[A, G, RSeq, RSeq[A, G]] { self =>
  sealed trait RSeqOperation
  case class Append(elem: A)                                           extends RSeqOperation
  case class Prepend(elem: A)                                          extends RSeqOperation
  case class Insert(index: Int, elem: A)                               extends RSeqOperation
  case class Remove(index: Int)                                        extends RSeqOperation
  case class RemoveElem(elem: A)                                       extends RSeqOperation
  case class Update(index: Int, elem: A)                               extends RSeqOperation
  case class Combined(indexRemoval: Int, indexInsertion: Int, elem: A) extends RSeqOperation

  case class AppendAll(elems: IterableOnce[A])                                                 extends RSeqOperation
  case class PrependAll(elems: IterableOnce[A])                                                extends RSeqOperation
  case class InsertAll(index: Int, elems: IterableOnce[A])                                     extends RSeqOperation
  case class RemoveAll(index: Int, count: Int)                                                 extends RSeqOperation
  case class RemoveAllElems(elems: IterableOnce[A])                                            extends RSeqOperation
  case class Patch(index: Int, other: IterableOnce[A], replaced: Int)                          extends RSeqOperation
  case class MassUpdate(indicesRemoved: IterableOnce[Int], insertions: IterableOnce[(Int, A)]) extends RSeqOperation

  type E = RSeqOperation

  def map[B, J[x] >: G[x] : Functor](f: A => B): RSeq[B, J] = new RSeq[B, J] {
    override def stream: J[RSeqOperation] = Functor[J].map(self.stream) {
      case self.Append(elem)                                 => Append(f(elem))
      case self.Prepend(elem)                                => Prepend(f(elem))
      case self.Insert(index, elem)                          => Insert(index, f(elem))
      case self.Remove(index)                                => Remove(index)
      case self.RemoveElem(elem)                             => RemoveElem(f(elem))
      case self.Update(index, elem)                          => Update(index, f(elem))
      case self.Combined(indexRemoval, indexInsertion, elem) => Combined(indexRemoval, indexInsertion, f(elem))
      case self.AppendAll(elems)                             => AppendAll(elems.iterator.map(f))
      case self.PrependAll(elems)                            => PrependAll(elems.iterator.map(f))
      case self.InsertAll(index, elems)                      => InsertAll(index, elems.iterator.map(f))
      case self.RemoveAll(index, count)                      => RemoveAll(index, count)
      case self.RemoveAllElems(elems)                        => RemoveAllElems(elems.iterator.map(f))
      case self.Patch(index, other, replaced)                => Patch(index, other.iterator.map(f), replaced)
      case self.MassUpdate(indicesRemoved, insertions) =>
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
