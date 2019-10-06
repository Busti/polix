package polix.collection.variable

trait VGrowable[-A] {
  def addOne(elem: A): this.type

  @inline final def += (elem: A): this.type = addOne(elem)

  def addAll(elems: IterableOnce[A]): this.type

  @inline final def ++= (elems: IterableOnce[A]): this.type = addAll(elems)
}
