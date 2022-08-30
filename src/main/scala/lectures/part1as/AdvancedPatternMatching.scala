package com.hoc081098.udemyscalaadvanced
package lectures.part1as

object AdvancedPatternMatching extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ =>
  }

  /*
    - constants
    - wildcards
    - case classes
    - tuples
    - some special magic like above
  */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  //  object PersonPattern {
  //    def unapply(person: Person): Option[(String, Int)] =
  //      if (person.age < 21) None
  //      else Some((person.name, person.age))
  //  }
  //  new Person("Bob", 20) match {
  //    case PersonPattern(n, a) => s"Hi, my name is $n and I am $a yo."
  //  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a yo."
  }
  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /*
    Exercise.
  */
  val n: Int = 8
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an event number"
    case _ => "no property"
  }

  // my implementation
  object MySingleDigit {
    def unapply(n: Int): Option[Unit] =
      if (n < 10) Some(())
      else None
  }

  object EventNumber {
    def unapply(n: Int): Option[Unit] =
      if (n % 2 == 0) Some(())
      else None
  }

  val mathProperty1 = n match {
    case MySingleDigit(_) => "single digit"
    case EventNumber(_) => "an event number"
    case _ => "no property"
  }

  // course's implementation
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val mathProperty2 = n match {
    case singleDigit() => "single digit"
    case even() => "an event number"
    case _ => "no property"
  }

  println(mathProperty)
  println(mathProperty1)
  println(mathProperty2)
}
