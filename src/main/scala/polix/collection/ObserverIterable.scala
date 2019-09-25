package polix.collection
import polix.execution.Ack

import scala.concurrent.Future

trait ObserverIterable[-A] {
  def onConcat(elem: A): Future[Ack]

  def onError(ex: Throwable): Unit

  def onComplete(): Unit
}
