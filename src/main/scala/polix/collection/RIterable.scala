package polix.collection

import polix.collection.internal.operators.RIterableMap
import polix.reactive.Source

trait RIterable[+A] extends RIterableOps[A, RIterable]

trait RIterableStream[+A] {
  protected trait RIterableEvent
  type E >: RIterableEvent

  case class Insertion(elem: A) extends RIterableEvent
  case class Removal(elem: A)   extends RIterableEvent

  def stream[G[_]: Source]: G[E]
}

trait RIterableOps[+A, +CC[_]] extends RIterableStream[A] {
  def rIterableFactory: RIterableFactory[CC]

  def map[B](f: A => B): CC[B] = rIterableFactory.from(
    new RIterableMap[A, B](this)
  )
}

trait RIterableFactory[+CC[_]] {
  def from[T](source: RIterableStream[T]): CC[T]
}
