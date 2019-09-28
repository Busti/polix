package polix.reactive

trait Sink[-F[_]] {
  def onNext[A](sink: F[A])(value: A): Unit
  def onError[A](sink: F[A])(error: Throwable): Unit

  type Subscription
}
