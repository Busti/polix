package polix.reactive

// todo: use simulacrum for typeclasses
trait Foldable2[F[_]] {
  def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): F[B]
}
