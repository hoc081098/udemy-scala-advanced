package com.hoc081098.udemyscalaadvanced
package lectures.part5ts

import lectures.part5ts

object TypeMembers extends App {
  class Animal

  class Dog extends Animal

  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ???

  // val cat: ac.BoundedAnimal = new Cat

  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // alternative to generics
  trait MyList {
    type T

    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int

    override def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat
  // new CatsType

  /**
   * Exercise - enforce a type to be applicable to SOME TYPES only
   */
  // LOCKED
  trait MList {
    type A

    def head: A

    def tail: MList
  }

  trait ApplicableToNumber {
    type A <: Number
  }

  // NOT OK
  //  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumber {
  //    override type A = String
  //
  //    override def head: String = hd
  //
  //    override def tail: CustomList = tl
  //  }

  // OK
  class IntList(hd: Integer, tl: IntList) extends MList with ApplicableToNumber {
    override type A = Integer

    override def head: Integer = hd

    override def tail: IntList = tl
  }

  // Number
  // type members and type member constraint (bounds)
}
