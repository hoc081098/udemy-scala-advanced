package com.hoc081098.udemyscalaadvanced
package lectures.part5ts

object FBoundedPolymorphism extends App {
  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal{
  //    override def breed: List[Animal] = ??? // List[Cat] !!
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Animal] = ??? // List[Dog] !!
  //  }

  //  // Solution 1 - naive
  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Cat] = ??? // List[Cat] !!
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Cat] = ??? // List[Dog] !!
  //  }

  // // Solution 2 - FBP
  //  trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] {
  //    override def breed: List[Animal[Cat]] = ??? // List[Cat] !!
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //  }
  //
  //  trait Entity[E <: Entity[E]] // ORM
  //
  //  class Person extends Comparable[Person] { // FBP
  //    override def compareTo(o: Person): Int = ???
  //  }
  //
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ???
  //  }

  //  // Solution 3 - FBP + self-types
  //  trait Animal[A <: Animal[A]] {
  //    this: A =>
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] {
  //    override def breed: List[Animal[Cat]] = ??? // List[Cat] !!
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //  }
  //
  //  trait Entity[E <: Entity[E]] // ORM
  //
  //  class Person extends Comparable[Person] { // FBP
  //    override def compareTo(o: Person): Int = ???
  //  }
  //
  //  //  class Crocodile extends Animal[Dog] {
  //  //    override def breed: List[Animal[Dog]] = ???
  //  //  }
  //
  //  trait Fish extends Animal[Fish]
  //
  //  class Shark extends Fish {
  //    override def breed: List[Animal[Fish]] = List(new Cod)
  //  }
  //
  //  class Cod extends Fish {
  //    override def breed: List[Animal[Fish]] = ???
  //  }

  //  // Solution 4 - type classes!
  //  trait Animal
  //
  //  trait CanBreed[A <: Animal] {
  //    def breed(a: A): List[A]
  //  }
  //
  //  class Dog extends Animal
  //
  //  object Dog {
  //    given dogsCanBreed: CanBreed[Dog] with {
  //      override def breed(a: Dog): List[Dog] = List()
  //    }
  //  }
  //
  //  extension[A <: Animal] (animal: A) {
  //    def breed(using canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
  //  }
  //
  //  val dog = new Dog
  //  dog.breed

  // Solution 5
  trait Animal[A] { // pure type class
    def breed(a: A): List[A]
  }

  class Dog

  object Dog:
    given dogAnimal: Animal[Dog] with
      override def breed(a: Dog): List[Dog] = Nil

  class Cat

  object Cat:
    given catAnimal: Animal[Cat] with
      override def breed(a: Cat): List[Cat] = Nil

  extension[A] (animal: A)
    def breed(using animalTypeClassInstance: Animal[A]): List[A] = animalTypeClassInstance.breed(animal)

  val dog = new Dog
  dog.breed

  val cat = new Cat
  cat.breed
}
