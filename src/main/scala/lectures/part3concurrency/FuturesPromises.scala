package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

import scala.concurrent.*
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration.*

// important for futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {
  def calculateMeaningOfLife: Int = {
    Thread.sleep(2_000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculate the meaning of life on ANOTHER thread
  } // (global) which is passed by the compiler

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"The meaning of life is $meaningOfLife")
    case Failure(exception) => println(s"I have failed with $exception")
  } // SOME thread

  Thread.sleep(3_000)

  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching from database
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // Client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  //  mark.onComplete {
  //    case Success(markProfile) => {
  //      val bill = SocialNetwork.fetchBestFriend(markProfile)
  //      bill.onComplete {
  //        case Success(billProfile) => markProfile.poke(billProfile)
  //        case Failure(exception) => exception.printStackTrace()
  //      }
  //    }
  //    case Failure(exception) => exception.printStackTrace()
  //  }

  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall: Future[String] = mark.map(profile => profile.name)

  val marksBestFriend: Future[Profile] = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val zucksBestFriendRestricted: Future[Profile] = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1_000)

  // fallback
  val aProfileNoMatterWhat: Future[Profile] = SocialNetwork
    .fetchProfile("unknown-id")
    .recover {
      case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
    }

  val aFetchedProfileNoMatterWhat = SocialNetwork
    .fetchProfile("unknown-id")
    .recoverWith {
      case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }

  val fallbackResult = SocialNetwork
    .fetchProfile("unknown-id")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1_000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from DB
      // create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions -> pimp my library
    }
  }

  println(BankingApp.purchase("hoc081098", "iPhone 12", "rock the JVM store", 3_000))

  // promises
  val promise = Promise[Int]() // "controller" over a future
  val future: Future[Int] = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println(s"[consumer] I've received $r")
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    //    promise.failure(new RuntimeException())
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1_000)
}