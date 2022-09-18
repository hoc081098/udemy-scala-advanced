package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

// TYPE CLASS
trait MyTypeClassTemplate[T] {
  def action(value: T): String
}

object MyTypeClassTemplate {
  def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance
}
