package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

import javax.swing.text.AbstractDocument.Content

object TypeClasses extends App {
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email /> </div>"
  }

  User("John", 32, "john@rockthejvm.com").toHtml

  /*
    1 - for the types WE write
    2 - ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any): String = value match {
      case User(n, a, e) => ???
      case d: java.util.Date => ???
      case _ => ???
    }
  }

  /*
    1 - lost the type safety
    2 - need to modify the code every time
    3 - still ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email} /> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  // 1 - we can define serializers for other types

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer serialize value

    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style=\"background-color: blue\">$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println("----")
  println(john.toHTML(UserSerializer)) // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  println(john.toHTML)
  // COOL!
  /*
    - extend to new types
    - choose implementation
    - super expressive!
   */

  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))

  /*
    - type class itself --- HTMLSerializer[T] { ... }
    - type class instances (some of which are implicit) --- UserSerializer, IntSerializer
    - conversion with implicit classes --- HTMLEnrichment
   */

  // context bounds
  def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body> ${content.toHTML(serializer)}</body></html>"

  def htmlSugar[T: HTMLSerializer](content: T): String =
    s"<html><body> ${content.toHTML}</body></html>"

  def htmlSugar2[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body> ${content.toHTML(serializer)}</body></html>"
  }

  // implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code
  val standardPerms = implicitly[Permissions]

  // takeaways
  // type class
  // type class instances (often implicit)
  // invoking type class instances: apply()
  // enriching types with type classes
}
