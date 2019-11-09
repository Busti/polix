package polix.collection.internal.operators

import scala.language.higherKinds

import cats.Functor
import polix.collection.RSeq
import polix.collection.RSeqMutations._

class OperatorMap[A, B, G[_], G2[x] >: G[x] : Functor](
  source: RSeq[A, G],
  f: A => B
) extends RSeq[B, G2] {
  type M = RSeqMutation[B]

  override def stream: G2[RSeqMutation[B]] = Functor[G2].map(source.stream) {
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
