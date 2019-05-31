package polix.collection
import scala.language.higherKinds

trait RSeq[+A, R[_]] extends RIterable[A, R] with RSeqOps[A, R, RSeq, RSeq[A, R]]

trait RSeqOps[+A, R[_], +CC[_, _[_]], +C] extends RIterableOps[A, R, CC, C] {
  def apply(i: Int): R[Option[A]]

  def contains[B >: A](elem: B): R[Boolean]

  def containsSlice[B >: A](that: Seq[B]): R[Boolean]

  def distinct: C

  def distinctBy[B](f: A => B): C

  def indexOf[B >: A](elem: B, from: Int = 0): R[Int]

  def indexOfSlice[B >: A](that: Seq[B], from: Int = 0): R[Int]

  def indexWhere(p: A => Boolean, from: Int = 0): R[Int]

  def indices: R[Range]

  def isDefinedAt(idx: Int): R[Boolean]

  def lastIndexOf[B >: A](elem: B, end: Int): R[Int]

  def lastIndexOf[B >: A](elem: B): R[Int]

  def lastIndexOfSlice[B >: A](that: Seq[B], end: Int): R[Int]

  def lastIndexOfSlice[B >: A](that: Seq[B]): R[Int]

  def lastIndexWhere(p: A => Boolean, end: Int): R[Int]

  def lastIndexWhere(p: A => Boolean): R[Int]

  def length: R[Int]

  def padTo[B >: A](len: Int, elem: B): CC[B, R]

  def reverse: C

  def segmentLength(p: A => Boolean, from: Int = 0): R[Int]

  def sortBy[B](f: A => B)(implicit ord: Ordering[B]): C = sorted(ord on f)

  def sortWith(lt: (A, A) => Boolean): C = sorted(Ordering.fromLessThan(lt))

  def sorted[B >: A](implicit ord: Ordering[B]): C
}
