package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

object ExtensionMethods extends App {
  case class Person(name: String) {
    def greet: String = s"Hi, I'm $name, how can I help?"
  }

  extension (string: String) { // extension method
    def greetAsPerson: String = Person(string).greet
  }

  val greet: String = "hoc081098".greetAsPerson
  println(greet)

  // extension methods <=> implicit classes

  object Scala2ExtensionMethods {
    implicit class RichInt(value: Int) extends AnyVal {
      def isEven: Boolean = value % 2 == 0

      def sqrt: Double = Math.sqrt(value)

      def times(f: () => Unit): Unit = (1 to value).foreach(_ => f())
    }
  }

  val is3Even = 3.isEven

  extension (value: Int) {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(f: () => Unit): Unit = (1 to value).foreach(_ => f())
  }

  // generic extensions
  extension[A] (list: List[A]) {
    def ends: (A, A) = (list.head, list.last)
    def extremes(using ordering: Ordering[A]): (A, A) = list.sorted.ends // <-- can call an extension method here

  }
  val ends = List(1, 2).ends
  val extremes = List(3, 1, 2).extremes
  println(ends)
  println(extremes)
}
