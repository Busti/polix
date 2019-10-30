package polix.collection.variable

import scala.language.higherKinds
import polix.collection.{MutationSink, RSeq}
import polix.reactive.Sink
import polix.collection.RSeqMutations._

abstract class VBuffer[A, G[_] : Sink] extends RSeq[A, G] with MutationSink[G] with VGrowable[A] with VShrinkable[A] {
  override def streamMutation(mutation: M)(G: Sink[G]): Unit = Sink[G].onNext(stream)(mutation)

  override def addOne(elem: A): this.type = {
    streamMutation(Append(elem))
    this
  }

  @inline final def append(elem: A): this.type = addOne(elem)

  def prepend(elem: A): this.type = {
    streamMutation(Prepend(elem))
    this
  }

  @inline final def +=:(elem: A): this.type = prepend(elem)

  override def addAll(elems: IterableOnce[A]): this.type = {
    streamMutation(AppendAll(elems))
    this
  }

  @inline final def appendAll(elems: IterableOnce[A]): this.type = addAll(elems)

  def prependAll(elems: IterableOnce[A]): this.type = {
    streamMutation(PrependAll(elems))
    this
  }

  @inline final def ++=:(elems: IterableOnce[A]): this.type = prependAll(elems)

  def insert(index: Int, elem: A): Unit = streamMutation(Insert(index, elem))

  def insertAll(index: Int, elems: IterableOnce[A]): Unit = streamMutation(InsertAll(index, elems))

  def remove(index: Int): Unit = streamMutation(Remove(index))

  def remove(index: Int, count: Int): Unit = streamMutation(RemoveAll(index, count))

  override def subtractOne(elem: A): this.type = {
    streamMutation(RemoveElem(elem))
    this
  }

  @inline final def remove(elem: A): this.type = subtractOne(elem)

  override def subtractAll(elems: IterableOnce[A]): this.type = {
    streamMutation(RemoveAllElems(elems))
    this
  }

  @inline final def remove(elems: IterableOnce[A]): this.type = subtractAll(elems)

  def patchInPlace(from: Int, patch: IterableOnce[A], replaced: Int): this.type = {
    streamMutation(Patch(from, patch, replaced))
    this
  }
}
