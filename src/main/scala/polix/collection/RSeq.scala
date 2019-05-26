package polix.collection
import scala.language.higherKinds

trait RSeq[+A, R[+ _]] extends RIterable[A, R] with RSeqOps[A, R, RSeq, RSeq[A, R]]

trait RSeqOps[+A, R[+ _], +CC[_, _[+ _]], +C] extends RIterableOps[A, R, CC, C] { this: RSeq[A, R] =>

}
