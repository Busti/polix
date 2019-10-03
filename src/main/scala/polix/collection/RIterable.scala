package polix.collection

import polix.reactive.Source

trait RIterable[+A, +G[_]] extends RIterableOps[A, G, ({ type L[+T] = RIterable[T, G]})#L, RIterable[A, G]] {
  def stream(implicit G: Source[G]): G[E]
}

sealed trait RIterableOps[+A, +G[_], +CC[_], +C] {
  type E

  def map[B >: A](f: A => B): CC[B]
}
