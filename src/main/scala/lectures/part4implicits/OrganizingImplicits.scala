package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

object OrganizingImplicits extends App {
  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  // implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  println(List(1, 4, 5, 3, 2).sorted)

  // scala.Predef

  /*
    Implicits (used as implicit parameters):
      - val/var
      - object
      - accessor methods = defs with NO parentheses (IMPORTANT!)
   */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  //  object Person {
  //    implicit val alphabeticPersonOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  //  }
  //
  //  implicit val agePersonOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  //  println(persons.sorted)

  /*
    Implicit scope
      - normal scope = LOCAL SCOPE
      - imported scope
      - companions of all types involved in the method signature
        - List
        - Ordering
        - all the types involved = A or any supertype
   */

  // def sorted[B >: A](implicit ord: Ordering[B]): List[B]

  object AlphabeticNameOrdering {
    implicit val alphabeticPersonOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  }

  object AgeOrdering {
    implicit val agePersonOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }

  import AgeOrdering._

  println(persons.sorted)

  /*
    Exercise:
      - totalPrice = most used (50%)
      - by unit count = 25%
      - by unit price = 25%
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan { (a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice }
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits < _.nUnits)

  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

  import UnitCountOrdering._

  println(
    List(
      Purchase(10, 5),
      Purchase(2, 300),
      Purchase(5, 500),
    ).sorted
  )
}
