package polix.collection.internal.operators
import cats.Functor
import polix.collection.RIterable

class OperatorMap[+A, +B, R[+ _]: Functor, CC[_, _[+ _]]](f: A => B)(other: RIterable[A, R]) extends RIterable[B, R] {
  override def concatenations: R[R[B]] = ???
}
