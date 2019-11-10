package polix.collection

import scala.language.higherKinds
import scala.language.reflectiveCalls
import cats.Functor
import polix.reactive.Scannable

trait RIterableOps[+G[_], +A, +CC[_[_], _], +C] {
  type M

  def map[G2[x] >: G[x] : Functor, B](f: A => B): CC[G2, B]

  def sorted[G2[x] >: G[x] : Scannable, A2 >: A](implicit ord: Ordering[A2]): CC[G2, A2]
}

trait RIterable[+G[_], +A] extends RIterableOps[G, A, RIterable, RIterable[G, A]] {
  def stream: G[M]
}
