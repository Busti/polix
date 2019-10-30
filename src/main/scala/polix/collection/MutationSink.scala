package polix.collection

import polix.reactive.Sink

import scala.language.higherKinds

trait MutationSink[G[_]] {
  type M

  def streamMutation(mutation: M)(implicit G : Sink[G])
}
