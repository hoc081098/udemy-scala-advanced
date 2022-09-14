package com.hoc081098.udemyscalaadvanced
package playground

import scala.collection.immutable.Map

trait Car {
  def name: String

  def horsePower: Int
}

trait Marshaller extends Car {
  def toMap: Map[String, Any] = Map(
    "name" -> name,
    "horsePower" -> horsePower
  )
}

case class BMW(name: String, horsePower: Int) extends Car

@main
def main(): Unit = {
  val bmw: BMW with Marshaller = new BMW("F15", 309) with Marshaller
  val bmw1: BMW & Marshaller = new BMW("F15", 309) with Marshaller

  println(bmw.toMap)
  println(bmw1.toMap)

  println("Hello world!")

  println(
    LazyList.unfold(0)(x => Some(x, x + 1))
      .take(100)
      .toList
  )
  println(
    LazyList.iterate(0)(_ + 1)
      .take(100)
      .toList
  )
}