package com.hoc081098.udemyscalaadvanced
package lectures.part5ts

import sun.security.rsa.RSAUtil.KeyType

object PathDependentTypes extends App {
  class Outer {
    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner) = println(i)

    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass(val i: Int)
    type HelperType = String
    println(new HelperClass(1))
    2
  }

  def anotherMethod(): Unit = {
    class HelperClass(val s: String)
    println(new HelperClass("1"))
  }

  aMethod
  anotherMethod()

  // per-instance
  val o = new Outer
  val inner = new o.Inner // o.Inner is a TYPE

  val oo = new Outer
  // val otherInner: oo.Inner = new o.Inner

  o.print(inner)
  // oo.print(inner)

  // path-dependent types

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /**
   * Exercise
   * DB keyed by Int or String, but maybe others
   */

  /*
    Use path-dependent types
    abstract type members and/or type aliases
   */
  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int] {
    override type Key = Int
  }

  trait StringItem extends Item[String] {
    override type Key = String
  }

  // only works on scala 2
  //  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???
  //
  //  get[IntItem](42) // ok
  //  get[StringItem]("home") // ok
  //  get[IntItem]("scala") // not ok
}
