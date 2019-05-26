package polix.collection

trait RMap[K, +V, R[+ _]] extends RIterable[(K, V), R] with RMapOps[K, V, R, RMap, RMap[K, V, R]]

trait RMapOps[K, +V, R[+_], +CC[_, _[+_]], +C] extends RIterableOps[(K, V), R, CC, C] { this: RMap[K, V, R] =>

}