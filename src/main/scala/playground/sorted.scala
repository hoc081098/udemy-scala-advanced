package com.hoc081098.udemyscalaadvanced
package playground

object sorted extends App {
  given Ordering[Int] with {
    override def compare(x: Int, y: Int): Int = {
      println(s"compare x=$x, y=$y")
      y - x
    }
  }

  val value: List[List[Int]] = List(
    List(1, 2, 3),
    List(4, 5, 6)
  )
  println(value.sorted)

  val ordering = implicitly[Ordering[Iterable[Int]]]
  println(ordering)
}
