package polix.collection.variable

import cats.Applicative

import scala.language.higherKinds
import polix.collection.RSeq
import polix.reactive.Sink
import monix.reactive.subjects._
import polix.collection.RSeqMutations._

abstract class VBuffer[A, G[_] : Sink] extends RSeq[A, G] with VGrowable[A] with VShrinkable[A] {
  def streamSingle(mutation: RSeqMutation[A]): Unit = Sink[G].onNext(stream)(Iterable.single(mutation))

  def streamAll(mutations: IterableOnce[RSeqMutation[A]]): Unit = Sink[G].onNext(stream)(mutations)

  override def addOne(elem: A): this.type = {
    streamSingle(Append(elem))
    this
  }

  @inline final def append(elem: A): this.type = addOne(elem)

  def prepend(elem: A): this.type = {
    streamSingle(Prepend(elem))
    this
  }

  @inline final def +=: (elem: A): this.type = prepend(elem)

  override def addAll(elems: IterableOnce[A]): this.type = {
    streamAll(elems.iterator.map(Append.apply))
    this
  }

  @inline final def appendAll(elems: IterableOnce[A]): this.type = addAll(elems)

  def prependAll(elems: IterableOnce[A]): this.type = {
    streamAll(elems.iterator.map(Prepend.apply))
    this
  }

  @inline final def ++=: (elems: IterableOnce[A]): this.type = prependAll(elems)

  def insert(index: Int, elem: A): Unit = streamSingle(Insert(index, elem))

  def insertAll(index: Int, elems: IterableOnce[A]): Unit = streamAll(
    elems.iterator.zipWithIndex.map { case (elem, j) => Insert(index + j, elem) }
  )

  def remove(index: Int): Unit = streamSingle(RemoveIndex(index))

  def remove(index: Int, count: Int): Unit = streamAll(
    Iterator.tabulate(count)(j => RemoveIndex(index + j))
  )

  override def subtractOne(elem: A): this.type = {
    streamSingle(RemoveElem(elem))
    this
  }

  @inline final def remove(elem: A): this.type = subtractOne(elem)

  override def subtractAll(elems: IterableOnce[A]): this.type = {
    streamAll(elems.iterator.map(RemoveElem.apply))
    this
  }

  @inline final def remove(elems: IterableOnce[A]): this.type = subtractAll(elems)

  def patchInPlace(from: Int, patch: IterableOnce[A], replaced: Int): this.type = {
    Iterator.tabulate() // How do we solve this?
    Sink[G].onNext(stream)(Patch(from, patch, replaced))
    this
  }
}
