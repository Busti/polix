package polix.collection.internal.operators

import polix.collection.{RIterable, RIterableFactory, RIterableOps, RIterableStream}
import polix.reactive.Source

private[collection] final class RIterableMap[+A, +B](underlying: RIterableStream[A]) extends RIterableStream[B] {
  override type E = this.type

  override def stream[G[_] : Source]: G[RIterableMap.this.type] = ???
}
