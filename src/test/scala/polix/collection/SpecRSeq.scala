package polix.collection

import org.scalatest._
import org.scalatest.WordSpec
import org.scalatest.Assertions._
import org.scalatest.Matchers._
import monix.execution.ExecutionModel.SynchronousExecution
import monix.execution.Scheduler
import monix.execution.schedulers.TrampolineScheduler
import monix.reactive._
import monix.reactive.subjects._
import polix.collection.RSeqMutations._

class SpecRSeq extends WordSpec {
  implicit val scheduler = TrampolineScheduler(Scheduler.global, SynchronousExecution)

  "RSeq" when {
    val input: PublishSubject[RSeqMutation[Int]] = PublishSubject()
    val rseq: RSeq[Observable, Int]              = RSeq.lift(input)

    "mapped" should {
      val result: RSeq[Observable, Int] = rseq.map(n => n * 2)

      var test: RSeqMutation[Int] = null
      val output                  = RSeq.drop(result)
      output.foreach(mutation => test = mutation)

      "map Append" in {
        input onNext Append(1)
        test shouldBe Append(2)
      }

      "map Prepend" in {
        input onNext Prepend(1)
        test shouldBe Prepend(2)
      }
    }
  }
}
