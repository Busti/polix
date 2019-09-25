package polix.execution

sealed trait Ack

object Ack {
  case object Continue extends Ack
  case object Stop     extends Ack
}
