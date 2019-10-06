package polix.collection.variable

import cats.Applicative

import scala.language.higherKinds
import polix.collection.RSeq
import polix.reactive.Sink
import monix.reactive._

abstract class VBuffer[A, G[_] : Sink] extends RSeq[A, G] with VGrowable[A] with VShrinkable[A] {
  override def addOne(elem: A): this.type = {
    Sink[G].onNext(stream)(Append(elem))
    this
  }

  @inline final def append(elem: A): this.type = addOne(elem)

  def prepend(elem: A): this.type = {
    Sink[G].onNext(stream)(Prepend(elem))
    this
  }

  @inline final def +=: (elem: A): this.type = prepend(elem)

  override def addAll(elems: IterableOnce[A]): this.type = {
    Sink[G].onNext(stream)(AppendAll(elems))
    this
  }

  @inline final def appendAll(elems: IterableOnce[A]): this.type = addAll(elems)

  def prependAll(elems: IterableOnce[A]): this.type = {
    Sink[G].onNext(stream)(PrependAll(elems))
    this
  }

  @inline final def ++=: (elems: IterableOnce[A]): this.type = prependAll(elems)

  def insert(index: Int, elem: A): Unit = Sink[G].onNext(stream)(Insert(index, elem))

  def insertAll(index: Int, elems: IterableOnce[A]): Unit = Sink[G].onNext(stream)(InsertAll(index, elems))

  def remove(index: Int): Unit = Sink[G].onNext(stream)(Remove(index))

  def remove(index: Int, count: Int): Unit = Sink[G].onNext(stream)(RemoveAll(index, count))

  override def subtractOne(elem: A): this.type = {
    Sink[G].onNext(stream)(RemoveElem(elem))
    this
  }

  @inline final def remove(elem: A): this.type = subtractOne(elem)

  override def subtractAll(elems: IterableOnce[A]): this.type = {
    Sink[G].onNext(stream)(RemoveAllElems(elems))
    this
  }

  @inline final def remove(elems: IterableOnce[A]): this.type = subtractAll(elems)
}
