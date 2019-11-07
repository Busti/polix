package polix.collection

import org.scalatest._
import org.scalatest.WordSpec
import org.scalatest.Assertions._

import monix.execution.ExecutionModel.SynchronousExecution
import monix.execution.Scheduler
import monix.execution.schedulers.TrampolineScheduler
import monix.reactive._
import monix.reactive.subjects._
import polix.reactive.Scannable

class SpecRSeq extends WordSpec {
  implicit val scheduler = TrampolineScheduler(Scheduler.global, SynchronousExecution)

  "something" when {
    "asdasd" should {
      "asdasd" in {
        assert(1 == 1)
      }
    }
  }
}
