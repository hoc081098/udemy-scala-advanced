package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

object Givens extends App {
  val aList = List(2, 3, 4, 1)
  val anOrderedList = aList.sorted // implicit Ordering[Int]

  // Scala 2 style
  object Implicits {
    implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  // Scala 3 style
  object Givens {
    given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    // given <=> implicit vals
  }

  // instantiating an anonymous class
  object GivenAnonymousClassNaive {
    given descendingOrdering_v2: Ordering[Int] = new Ordering[Int] {
      override def compare(x: Int, y: Int): Int = y - x
    }
  }

  object GivenWith {
    given descendingOrdering_v3: Ordering[Int] with {
      override def compare(x: Int, y: Int): Int = {
        println(s"giveWith compare $x, $y")
        y - x
      }
    }
  }

  import GivenWith._ // in Scala3, this import does NOT import givens as well

  import GivenWith.given // imports all givens

  // import GivenWith.descendingOrdering_v3

  // implicits arguments <=> using clauses

  def extremes[A](list: List[A])(implicit ordering: Ordering[A]): (A, A) = {
    val sortedList = list.sorted
    sortedList.head -> sortedList.last
  }

  def extremes_v2[A](list: List[A])(using ordering: Ordering[A]): (A, A) = {
    val sortedList = list.sorted // (ordering)
    sortedList.head -> sortedList.last
  }

  // implicit def (synthesize new implicit values)
  trait Combinator[A] { // Semigroup
    def combine(x: A, y: A): A
  }

  //  implicit def listOrdering[A](implicit simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] =
  //    (x: List[A], y: List[A]) =>
  //      val sumX = x.reduce(combinator.combine)
  //      val sumY = y.reduce(combinator.combine)
  //      simpleOrdering.compare(sumX, sumY)

  // equivalent in Scala 3 with givens
  given listOrdering_v2[A] (using simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] with
    override def compare(x: List[A], y: List[A]): Int =
      println(s"simpleOrdering: $simpleOrdering")
      println(s"combinator: $combinator")

      val sumX = x.reduce(combinator.combine)
      val sumY = y.reduce(combinator.combine)

      simpleOrdering.compare(sumX, sumY)

  // implicit conversions (abused in Scala 2)
  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name."
  }

  //  implicit def stringToPerson(string: String): Person = Person(string)
  //  val greet = "hoc081098".greet() // stringToPerson("hoc081098").greet()

  // in Scala 3

  import scala.language.implicitConversions // required in Scala 3

  given stringToPersonConversion: Conversion[String, Person] with
    override def apply(x: String): Person = Person(x)

  println("hoc081098".greet())
  println(anOrderedList)

  object CombinatorGivens {
    given sumIntCombinator: Combinator[Int] with {
      override def combine(x: Int, y: Int): Int = {
        println(s"SumIntCombinator $x, $y")
        x + y
      }
    }
  }

  import CombinatorGivens.sumIntCombinator

  println(s"list ordering: ${implicitly[Ordering[List[Int]]]}")
  val value: List[List[Int]] = List(
    List(1, 2, 3),
    List(4, 5, 6)
  )
  println(value.sorted)
}

