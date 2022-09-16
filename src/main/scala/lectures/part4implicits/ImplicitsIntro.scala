package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

object ImplicitsIntro extends App {
  val pair = "hoc081098" -> "555"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // println(fromStringToPerson("Peter").greet)

  //  class A {
  //    def greet: Int = 2
  //  }
  //
  //  implicit def fromStringToA(str: String): A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount

  implicit val defaultAmount: Int = 10

  increment(2)
  // NOT default args


}
