package polix.reactive

sealed trait Subscription {
  def cancel(): Unit
}