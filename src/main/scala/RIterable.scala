import cats.{Applicative, Monad}

import scala.annotation.unchecked.uncheckedVariance
import scala.language.higherKinds

trait RIterable[+A, R[+_]] extends RIterableOps[A, R, RIterable, RIterable[A, R]]

/**
  *
  * @tparam A
  * @tparam R
  */
trait RIterableOps[+A, R[+_], +CC[_, _[+_]], +C] { this: RIterable[A, R] =>
  def collect[B](pf: PartialFunction[A, B]): CC[B, R]

  def collectFirst[B](pf: PartialFunction[A, B]): R[Option[B]]

  def corresponds[B](that: RIterable[B, R])(p: (A, B) => Boolean): R[Boolean]

  def count(p: A => Boolean): R[Boolean]

  def drop(n: Int): C

  def dropWhile(p: A => Boolean): C

  def exists(p: A => Boolean): R[Boolean]

  def filter(p: A => Boolean): C

  def filterNot(p: A => Boolean): C

  def find(p: A => Boolean): R[Option[A @uncheckedVariance]]

  def flatMap[B](f: A => RIterable[B, R]): CC[B, R]

  def flatten[B](implicit asRIterable: A => RIterable[B, R]): CC[B, R]

  def foldUndo[B](z: B)(op: (B, A) => B)(undo: (B, A) => B): R[B]

  def forall(p: A => Boolean): R[Boolean]

  def isEmpty: R[Boolean]

  def knownSize: R[Int]

  def map[B](f: A => B): CC[B, R]

  def maxByOption[B >: A](f: A => B)(implicit cmp: Ordering[B]): R[Option[B]]

  def maxOption[B >: A](implicit ord: Ordering[B]): R[Option[B]]

  def minByOption[B >: A](f: A => B)(implicit cmp: Ordering[B]): R[Option[B]]

  def minOption[B >: A](implicit ord: Ordering[B]): R[Option[B]]

  def nonEmpty: R[Boolean]

  def reduceUndo[B >: A](op: (B, B) => B)(undo: (B, B) => B): R[B]

  def reduceUndoOption[B >: A](op: (B, B) => B)(undo: (B, B) => B): R[Option[B]]

  def size: R[Int]

  def slice(from: Int, to: Int): C

  def span(p: A => Boolean): (C, C)

  def sum[B >: A](implicit num: Numeric[B]): R[B] =
    foldUndo(num.zero)(num.plus)(num.minus)

  def take(n: Int): C

  def takeWhile(p: A => Boolean): C

  def zipWithIndex: CC[(A @uncheckedVariance, Int), R]

  def head: R[A]

  def headOption: R[Option[A]]

  def last: R[A]

  def lastOption: R[Option[A]]
}
