package polix.collection

import polix.reactive.Source

trait RSeq[A] extends RIterable[A] {
  sealed trait RSeqEvents extends RIterableEvent
  override type E = RSeqEvents

  case class Prepend(elem: A)                                        extends RSeqEvents
  case class Append(elem: A)                                         extends RSeqEvents
  case class Update(index: Int, elem: A)                             extends RSeqEvents
  case class Patch(index: Int, elem: IterableOnce[A], replaced: Int) extends RSeqEvents

  override def stream[G[_] : Source]: G[E]
}
