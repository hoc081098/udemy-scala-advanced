package com.hoc081098.udemyscalaadvanced
package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {
  /*
    Users, posts, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data types: Int, String, List, Date
    2 - type classes for conversion to intermediate data types
    3 - serialize to JSON
   */

  sealed trait JSONValue { // intermediate data types
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = s"\"$value\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values
      .map(_.stringify)
      .mkString(start = "[", sep = ",", end = "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
      {
        name: "John",
        age: 22,
        friends: [ ... ],
        latestPosts: {
          content: "Scala Rocks",
          date: ...
        }
      }
     */
    override def stringify: String = values
      .map { case (k, v) => s"\"$k\": ${v.stringify}" }
      .mkString(start = "{", sep = ",", end = "}")
  }

  val data = JSONObject(
    Map(
      "user" -> JSONString("hoc081098"),
      "posts" -> JSONArray(
        List(
          JSONString("Scala Rocks"),
          JSONNumber(453)
        )
      )
    )
  )

  println(data.stringify)

  // type class
  /*
    1 - type class
    2 - types class instances (implicit)
    3 - pimp library to use type class instances
   */
  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 2.3 conversion
  implicit class JsonOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = converter convert value
  }

  // 2.2

  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(
      Map(
        "name" -> JSONString(user.name),
        "age" -> JSONNumber(user.age),
        "email" -> JSONString(user.email)
      )
    )
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(
      Map(
        "content" -> JSONString(post.content),
        "createdAt" -> JSONString(post.createdAt.toString)
      )
    )
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(
      Map(
        "user" -> feed.user.toJSON,
        "posts" -> JSONArray(feed.posts.map(_.toJSON))
      )
    )
  }

  // call stringify on result

  val now = new Date(System.currentTimeMillis())
  val john = User("John", 42, "john@rockthejvm.com")
  val feed = Feed(
    john,
    List(
      Post("hello", now),
      Post("look at this cute puppy", now),
    )
  )

  println(feed.toJSON.stringify)
}
