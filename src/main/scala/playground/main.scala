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
  val bmw = new BMW("F15", 309) with Marshaller
  println(bmw.toMap)
  println("Hello world!")
}