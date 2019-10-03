package polix.collection

import scala.language.higherKinds
import scala.language.reflectiveCalls

import cats.Functor

trait RIterable[+A, +G[_]] extends RIterableOps[A, G, ({ type L[+T] = RIterable[T, G] })#L, RIterable[A, G]] {
  def stream: G[E]
}

sealed trait RIterableOps[+A, +G[_], +CC[_], +C] {
  type E

  def map[B, J[_] >: G[_] : Functor](f: A => B): CC[B]
}
