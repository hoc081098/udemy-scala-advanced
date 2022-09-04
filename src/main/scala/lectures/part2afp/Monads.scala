package com.hoc081098.udemyscalaadvanced
package lectures.part2afp

object Monads extends App {

  // our own Try monad
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
    left-identity
    unit(x).flatMap(f) = f(x)
    Attempt(x).flatMap(f) = f(x) // success case!
    Success(x).flatMap(f) = f(x) // proved

    right-identity
    attempt.flatMap(unit) = attempt
    Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
    Failure(e).flatMap(...) = Failure(e)

    associativity
    attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
    Failure(e).flatMap(f).flatMap(g) = Failure(e)
    Failure(e).flatMap(x => f(x).flatMap(g)) = Failure(e)

    Success(v).flatMap(f).flatMap(g) =
      f(v).flatMap(g) OR Failure(e)

    Success(v).flatMap(x => f(x).flatMap(g)) =
      f(v).flatMap(g) OR Failure(e)
   */

  val attempt = Attempt {
    throw new RuntimeException("My own monad, yes!")
  }
  println(attempt)

  /*
    EXERCISE:
    1. implement a Lazy[T] monad = computation which will only be executed when it's needed.
      unit/apply
      flatMap

    2. Monad = unit + flatMap
      Monads = unit + map + flatten
      Monad[T] {
        def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

        def map[B](f: T => B): Monad[B] = ???

        def flatten(m: Monad[[Monad[T]]): Monad[T] = ???

        (have List in mind)
      }
   */
}
