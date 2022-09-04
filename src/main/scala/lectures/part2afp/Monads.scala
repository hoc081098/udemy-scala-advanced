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

        def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x)))

        def flatten(m: Monad[[Monad[T]]): Monad[T] = flatMap(identity)

        (have List in mind)
      }
   */

  class Lazy[+T](value: => T) {
    lazy val use: T = value // call by need

    def flatMap[B](f: T => Lazy[B]): Lazy[B] = new Lazy[B](f(use).use)
  }

  val lazy1 = Lazy {
    println("Execute 1")
    1
  }
  val lazy2 = lazy1.flatMap { i =>
    Lazy {
      println("Execute 2")
      1 + i
    }
  }
  val lazy3 = lazy1.flatMap { i =>
    Lazy {
      println("Execute 3")
      1 + i
    }
  }

  println(lazy2.use)
  println(lazy3.use)

  /*
    left-identity
    unit.flatMap(f) = f(v)
    - Lazy(v).flatMap(f).v = Lazy(f(v).v).v = f(v).v = v
    - f(v).v = v

    right-identity
    l.flatMap(unit) = l
    Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

    associativity: l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
    Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
    Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */

  /*
  2. map and flatten in terms of flatMap
      Monad[T] {
          def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

          def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x)))

          def flatten(m: Monad[[Monad[T]]): Monad[T] = m.flatMap(identity)

          (have List in mind)
        }

  List(1, 2, 3).map(_ * 2) == List(1, 2, 3).flatMap(x => List(x * 2))
  List(List(1, 2), List(3, 4)).flatten == List(List(1, 2), List(3, 4)).flatMap(identity) == List(1, 2, 3, 4)
  */
}
