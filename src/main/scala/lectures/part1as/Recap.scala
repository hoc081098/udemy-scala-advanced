package com.hoc081098.udemyscalaadvanced
package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {
  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65
  // instruction vs expressions

  // compiler infers types for us
  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit = void
  val theUnit = println("hello, Scala")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack and tail
  @tailrec def factorial(n: Int, accumulator: Int = 1): Int =
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)

  println(factorial(0))
  println(factorial(1))
  println(factorial(2))
  println(factorial(3))
  println(factorial(10))

  // object-orientation programming
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog // subtype polymorphism

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // natural language

  1 + 2
  1.+(2)

  // anonymous classes
  val aCarnivore = new Carnivore :
    override def eat(a: Animal): Unit = println("roar!")

  // generics
  abstract class MyList[+A] // variance and variance problem

  // singletons and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally
  val throwsException: Nothing = throw new RuntimeException // Nothing
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: RuntimeException => "I caught an exception"
  } finally {
    println("Some logs")
  }

  // packaging and imports

  // functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  val incrementer2 = new Function[Int, Int] :
    override def apply(v1: Int): Int = v1 + 1

  incrementer.apply(1)
  incrementer(1)

  val anonymousIncrementer: Int => Int = (x: Int) => x + 1
  val anonymousIncrementer1: Function1[Int, Int] = (x: Int) => x + 1

  List(1, 2, 3).map(incrementer) // HOF
  List(1, 2, 3).map(anonymousIncrementer)
  List(1, 2, 3).map(anonymousIncrementer1)
  // map, flatMap, filter

  // for-comprehension
  val pairs: List[String] = for {
    num <- List(1, 2, 3) // if num > 2
    char <- List('a', 'b', 'c')
  } yield num + "-" + char
  println(pairs)

  val pairs1: List[String] = List(1, 2, 3).flatMap(num =>
    List('a', 'b', 'c')
      .map(char => num + "-" + char)
  )
  println(pairs == pairs1)

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples
  val tuple: (Int, Int) = 1 -> 2
  val tuple1: Tuple2[Int, Int] = 1 -> 2
  val tuple2 = (1, 2)
  tuple._1
  tuple._2

  val aMap: Map[String, Int] = Map(
    "Daniel" -> 789,
    "Jess" -> 555,
  )

  // "collections": Options, Try
  val anOption = Some(2)

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person(name = "Bob", age = 22)
  val greeting = bob match {
    case Person(n, _) => s"hi, my name is $n"
  }

  // all the pattern
}
