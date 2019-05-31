package polix.collection

import cats.Functor
import polix.collection.internal.operators.OperatorMap

import scala.annotation.unchecked.uncheckedVariance
import scala.language.higherKinds

trait RIterable[+A, R[_]] extends RIterableOps[A, R, RIterable, RIterable[A, R]] {
  def concatenations: R[R[A @uncheckedVariance]]
}

/**
  *
  * @tparam A
  * @tparam R
  */
trait RIterableOps[+A, R[_], +CC[_, _[_]], +C] {

  def rIterableFactory: RIterableFactory[R, CC] = ???

  def collect[B](pf: PartialFunction[A, B]): CC[B, R] = ???

  def collectFirst[B](pf: PartialFunction[A, B]): R[Option[B]] = ???

  def corresponds[B](that: RIterable[B, R])(p: (A, B) => Boolean): R[Boolean] = ???

  def count(p: A => Boolean): R[Boolean] = ???

  def drop(n: Int): C = ???

  def dropRight(n: Int): C = ???

  def dropWhile(p: A => Boolean): C = ???

  def exists(p: A => Boolean): R[Boolean] = ???

  def filter(p: A => Boolean): C = ???

  def filterNot(p: A => Boolean): C = ???

  def find(p: A => Boolean): R[Option[A @uncheckedVariance]] = ???

  def flatMap[B](f: A => RIterable[B, R]): CC[B, R] = ???

  def flatten[B](implicit asRIterable: A => RIterable[B, R]): CC[B, R] = ???

  def foldUndoLeft[B](z: B)(op: (B, A) => B)(undo: (B, A) => B): R[B] = ???

  def foldUndoRight[B](z: B)(op: (B, A) => B)(undo: (B, A) => B): R[B] = ???

  def forall(p: A => Boolean): R[Boolean] = ???

  def groupBy[K](f: A => K): RMap[K, C, R] = ???

  def groupMap[K, B](key: A => K)(f: A => B): RMap[K, CC[B, R], R] = ???

  def groupMapReduceUndo[K, B](key: A => K)(f: A => B)(reduce: (B, B) => B)(undo: (B, B) => B): RMap[K, B, R] = ???

  def grouped = ???

  def head: R[A @uncheckedVariance] = ???

  def headOption: R[Option[A]] = ???

  def init: C = ???

  def isEmpty: R[Boolean] = ???

  def last: R[A @uncheckedVariance] = ???

  def lastOption: R[Option[A]] = ???

  def map[B](f: A => B)(implicit R: Functor[R]): CC[B, R] = rIterableFactory.from(new OperatorMap())

  def maxByOption[B >: A](f: A => B)(implicit cmp: Ordering[B]): R[Option[B]] = ???

  def maxOption[B >: A](implicit ord: Ordering[B]): R[Option[B]] = ???

  def minByOption[B >: A](f: A => B)(implicit cmp: Ordering[B]): R[Option[B]] = ???

  def minOption[B >: A](implicit ord: Ordering[B]): R[Option[B]] = ???

  def nonEmpty: R[Boolean] = ???

  def partition(p: A => Boolean): (C, C) = ???

  def partitionWith[B1, B2](f: A => Either[B1, B2]): (CC[B1, R], CC[B2, R]) = ???

  def reduceUndo[B >: A](op: (B, B) => B)(undo: (B, B) => B): R[B] = ???

  def reduceUndoOption[B >: A](op: (B, B) => B)(undo: (B, B) => B): R[Option[B]] = ???

  def scanUndoLeft[B](z: B)(op: (B, A) => B): CC[B, R] = ???

  def scanUndoRight[B](z: B)(op: (B, A) => B): CC[B, R] = ???

  def size: R[Int] = ???

  def slice(from: Int, to: Int): C = ???

  def sliding = ???

  def span(p: A => Boolean): (C, C) = ???

  def splitAt(n: Int): (C, C) = ???

  def sum[B >: A](implicit num: Numeric[B]): R[B] = reduceUndo(num.plus)(num.minus) = ???

  def tail: C = ???

  def take(n: Int): C = ???

  def takeRight(n: Int): C = ???

  def takeWhile(p: A => Boolean): C = ???

  def zipWithIndex: CC[(A @uncheckedVariance, Int), R] = ???
}
