package polix.collection
import scala.language.higherKinds

trait RMap[K, +V, R[_]] extends RIterable[(K, V), R] with RMapOps[K, V, R, RMap, RMap[K, V, R]]

trait RMapOps[K, +V, R[_], +CC[_, _, _[_]], +C]
    extends RIterableOps[(K, V), R, RIterable, C] { this: RMap[K, V, R] =>

}
