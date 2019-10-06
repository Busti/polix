package polix.collection.variable

import scala.collection.mutable

trait VShrinkable[-A] extends mutable.Clearable {
  def subtractOne(elem: A): this.type

  @inline final def -= (elem: A): this.type = subtractOne(elem)

  def subtractAll(elems: IterableOnce[A]): this.type

  @inline final def --= (elems: IterableOnce[A]): this.type = subtractAll(elems)
}
