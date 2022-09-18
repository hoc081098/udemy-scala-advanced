package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

import scala.annotation.tailrec
import scala.language.implicitConversions

object PimpMyLibrary extends App {
  // 2.isPrime

  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(f: () => Unit): Unit = (1 to value).foreach(_ => f())

    def timesSolution(f: () => Unit): Unit = {
      @tailrec
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          f()
          timesAux(n - 1)
        }

      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = (1 to value).flatMap(_ => list).toList

    def `*Solution`[T](list: List[T]): List[T] = {
      @tailrec
      def concatenate(n: Int, acc: List[T]): List[T] =
        if (n <= 0) acc
        else concatenate(n - 1, acc ++ list)

      concatenate(value, Nil)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt
  42.isEven // new RichInt(42).isEven

  // type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._

  3.seconds

  // compiler doesn't do multiple implicit searches.
  // 42.isOdd

  /*
    Enrich the String class
    - asInt
    - encrypt
      "John" -> "Lqjp"
    Keep enriching the Int class
    - times(function)
      3.times(() => ...)
    - * List
      3 * List(1, 2) => List(1, 2, 1, 2, 1, 2)
   */

  implicit class RichString(value: String) extends AnyVal {
    def asInt: Int = Integer.parseInt(value)

    def encrypt(cypherDistance: Int): String = {
      val alphabet = 'a' to 'z'

      value.map(char => {
        if (char.isLower) alphabet((char - 'a' + cypherDistance) % alphabet.length)
        else alphabet((char.toLower - 'a' + cypherDistance) % alphabet.length).toUpper
      })
    }
  }

  println("123".asInt + 4)
  println("John".encrypt(2))

  println("-----")

  3.times { () => println("Hello") }
  println(3 * List(1, 2))

  println("-----")
  3.timesSolution { () => println("Hello") }
  println(3 `*Solution` List(1, 2))

  // "3" / 4
  implicit def stringToInt(string: String): Int = Integer.parseInt(string)

  println("6" / 2) // stringToInt("6") / 2

  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)

  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
    if (n) do something
    else do something else
   */
  val aConditionedValue = if (3) "Ok" else "Something wrong"
  println(aConditionedValue)

  /*
    Tips:
    - keep type enrichment to implicit classes and type classes
    - avoid implicit defs as much as possible
    - package implicits clearly, bring into scope only what you need
    - IF you need conversions, make the specify
   */
}
