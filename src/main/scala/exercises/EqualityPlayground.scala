package com.hoc081098.udemyscalaadvanced
package exercises

object EqualityPlayground extends App {
  case class User(name: String, age: Int, email: String)

  /**
   * Equality
   */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object UserNameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullUserEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  /*
    Exercise: implement the TC pattern for the Equality tc.
   */
  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer(a, b)
  }

  val john = User("John", 32, "john@rockthejvm.com")
  val anotherJohn: User = john.copy(age = 45, email = "anotherJohn@rockthejvm.com")

  UserNameEquality(john, anotherJohn)
  println(Equal(john, anotherJohn))
  // AD-HOC polymorphism

  println("---")

  /*
    Exercise - improve the Equal TC with an implicit conversion class
    ===(anotherValue: T)
    !==(anotherValue: T)
   */

  implicit class TypeSafeEqual[T](value: T) extends AnyVal {
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, other)

    def !==(other: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, other)
  }

  println(john === anotherJohn)
  println(john !== anotherJohn)

  /*
    john.===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJohn)(UserNameEquality)
   */
  /*
    TYPE SAFE
   */

  //  println(john === 43) TYPE SAFE
}
