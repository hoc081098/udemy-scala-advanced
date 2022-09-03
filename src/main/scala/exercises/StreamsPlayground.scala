package com.hoc081098.udemyscalaadvanced
package exercises

import scala.annotation.tailrec


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

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate two streams

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

  /*
    [1 2 3].toList([]) =
    [2 3].toList([1]) =
    [3].toList([2 1]) =
    [].toList([3 2 1]) =
    [3 2 1].reverse =
    [1 2 3]
   */
  // B super A
  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
}


object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons[A](
      start,
      MyStream.from(generator(start))(generator)
    )
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true

  def head: Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  //
  //
  //

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this)

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  //
  //
  //

  def foreach(f: Nothing => Unit): Unit = ()

  def map[B](f: Nothing => B): MyStream[B] = this

  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  //
  //
  //

  def take(n: Int): MyStream[Nothing] = this
}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = tl // call by need

  //
  //
  //

  /*
    val s = new Cons(1, EmptyStream)
    val prepended = 1 #:: s = new Cons(1, s)
   */
  def #::[B >: A](element: B): MyStream[B] = new Cons[B](element, this)

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream)

  //
  //
  //

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    s = new Cons(1, ?)
    mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_ + 1))
    ...mapped.tail
   */
  def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail.map(f)) // preserves lazy evaluation

  //  def flatMap[B](f: A => MyStream[B]): MyStream[B] = {
  //    val h = f(head)
  //    new Cons[B](h.head, h.tail ++ tail.flatMap(f))
  //  }

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): MyStream[A] = {
    if (predicate(head)) new Cons[A](head, tail.filter(predicate))
    else tail.filter(predicate) // preserves lazy evaluation!
  }

  //
  //
  //

  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons[A](head, tail.take(n - 1)) // preserves lazy evaluation!
}

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // naturals.#::(0)
  println(startFrom0.head)

  startFrom0.take(10_000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).take(20).toList())

  // Exercises on streams
  // 1 - stream of fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes's sieve
  /*
    [2 3 ... ]
    filter out all numbers divisible by 2
    [ 2 3 5 7 9 11 ...]
    filter out all numbers divisible by 3
    [ 2 3 5 7 11 ...]
    filter out all numbers divisible by 5
    ...
   */

  def primeNumbers(): MyStream[Int] = {
    def eratosthenesSieve(stream: MyStream[Int]): MyStream[Int] = {
      val head = stream.head

      new Cons(head, EmptyStream) ++ eratosthenesSieve(stream.tail.filter(_ % head != 0))
    }

    eratosthenesSieve(MyStream.from(2)(_ + 1))
  }

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    MyStream.from((first, second)) { tuple => tuple._2 -> (tuple._1 + tuple._2) }
      .map(_._1)

  println(fibonacci(0, 1).take(10).toList())
  println(primeNumbers().take(10).toList())
}
