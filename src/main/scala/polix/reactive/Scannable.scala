package polix.reactive

// todo: use simulacrum for typeclasses
trait Scannable[F[_]] {
  def scan[A, R](fa: F[A], seed: => R)(fn: (R, A) => R): F[R]

  def scanAccumulate[A, S, R](fa: F[A], seed: => S)(fn: (S, A) => (S, R)): F[R]
}

object Scannable {
  @inline def apply[F[_]](implicit scannable: Scannable[F]): Scannable[F] = scannable
}