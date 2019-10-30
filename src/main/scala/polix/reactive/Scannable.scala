package polix.reactive

// todo: use simulacrum for typeclasses
trait Scannable[F[_]] {
  def scan[A, B](fa: F[A], b: B)(f: (B, A) => B): F[B]
}

object Scannable {
  @inline def apply[F[_]](implicit scannable: Scannable[F]): Scannable[F] = scannable
}