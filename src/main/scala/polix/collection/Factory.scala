package polix.collection

trait RIterableFactory[R[+ _], +CC[_, _[+ _]]] {
  def from[A](source: RIterable[A, R]): CC[A, R]
}
