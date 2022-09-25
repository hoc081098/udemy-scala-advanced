package com.hoc081098.udemyscalaadvanced
package lectures.part5ts

object RockingInheritance extends App {
  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closable {
    def close(statusCode: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Closable with Writer[T]): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal {
    override def name: String = "lion"
  }

  trait Tiger extends Animal {
    override def name: String = "tiger"
  }

  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)

  /*
    Mutant
    extends Animal with { override def name: String = "lion" }
    with { override def name: String = "tiger" }

    LAST OVERRIDE GETS PICKED
   */

  // the super problem + type linearization

  trait Cold {
    def print(): Unit = println("cold")
  }

  trait Green extends Cold {
    override def print(): Unit = {
      println("green")
      super.print()
    }
  }

  trait Blue extends Cold {
    override def print(): Unit = {
      println("blue")
      super.print()
    }
  }

  class Red {
    def print(): Unit = println("red")
  }

  class White extends Red with Green with Blue {
    override def print(): Unit = {
      println("white")
      super.print()
    }
  }

  val color = new White
  color.print()

  /*
   *               Cold
   *                 |
   *           -----------
   *           |         |
   *           |         |
   *           |         |
   * Red     Green     Blue
   *  |        |         |
   *  |        |         |
   *  |        |         |
   *  --------------------
   *           |
   *         White
   *
   *  Cold = AnyRef with <Cold> (<Cold>: body of Cold)
   *  Green = Cold with <Green>
   *        = AnyRef with <Cold> with <Green>
   *  Blue  = Cold with <Blue>
   *        = AnyRef with <Cold> with <Blue>
   *  Red = AnyRef with <Red>
   *
   *  White = Red with Green with Blue with <White>
   *        = AnyRef with <Red>
   *          with (_AnyRef_ with <Cold> with <Green>)
   *          with (_AnyRef_ with _<Cold>_ with <Blue>)
   *          with <White>
   *        = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
   *        (type linearization)
   */
}
