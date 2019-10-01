package polix.reactive

trait Source[-F[_]] {
  def subscribe[G[_] : Sink, A](source: F[A])(sink: G[_ >: A]): Subscription
}