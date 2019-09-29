package polix.collection

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

  implicit def scannableObservable: Scannable[Observable] = new Scannable[Observable] {
    override def scan[A, B](fa: Observable[A], b: B)(f: (B, A) => B): Observable[B] = fa.scan(b)(f)
  }

  "An RSeq" when {
    "initialized through a subject" should {
      val input = PublishSubject[RSeqEvent[Int]]
      "materialize as expected" in {
        val rseq = RSeq.from[Int, Observable](input)

        val materialized = rseq.materialize

        var result = Seq.empty[Int]
        materialized.foreach(next => result = next)

        input.onNext(Insert(0, 0))
        assert(result === Seq(0))
      }
    }
  }
}
