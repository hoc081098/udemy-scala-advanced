package com.hoc081098.udemyscalaadvanced
package lectures.part2afp

object LazyEvaluation extends App {
  // lazy DELAYS the evaluation of values
  lazy val x: Int = {
    println("hello")
    42
  }

  println(x)
  println(x)

  // examples of implications:
  // side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // in conjunction with call by name
  def byNameMethod(n: => Int): Int = {
    // CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue = {
    // side effect or a long computation
    println("waiting...")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  // use lazy vals

  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less then 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater then 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  println

  val lt30Lazy = numbers.withFilter(lessThan30) // lazy vals under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  val r: List[Int] = for {
    a <- List(1, 2, 3) if a % 2 == 0 // use lazy vals!
  } yield a + 1

  val r1: List[Int] = List(1, 2, 3)
    .withFilter(_ % 2 == 0)
    .map(_ + 1)

  /*
    Exercise: implement a lazily evaluated, single linked STREAM of elements.

    naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
    naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream).
    naturals.foreach(println) // will crash - infinite!
    naturals.map(_ * 2) // stream of all event numbers (potentially infinite)
   */

  abstract class MyStream[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: MyStream[A]

    //
    //
    //

    def #::[B >: A](element: B): MyStream[B] // prepend operator

    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenate two streams

    //
    //
    //

    def foreach(f: A => Unit): Unit

    def map[B](f: A => B): MyStream[B]

    def flatMap[B](f: A => MyStream[B]): MyStream[B]

    def filter(predicate: A => Boolean): MyStream[A]

    //
    //
    //

    def take(n: Int): MyStream[A] // takes the first n elements out of the stream

    def takeAsList(n: Int): List[A]
  }


  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }
}
