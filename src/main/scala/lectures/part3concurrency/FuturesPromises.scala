package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

import java.util.concurrent.Executors
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}
import scala.concurrent.*
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration.*

object FuturesPromises extends App {
  // important for futures

  import scala.concurrent.ExecutionContext.Implicits.global

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
    println("[producer] fulfilling the promise")

    // "fulfilling" the promise
    promise.success(42)
    //    promise.failure(new RuntimeException())

    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1_000)
}

object Exercises extends App {
  implicit val ex: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(64))

  /*
    1) fulfill a future IMMEDIATELY with a value
    2) inSequence(fa, fb)
    3) first(fa, fb) => new future with the first value of the two futures
    4) last(fa, fb) => new future with the last value
    5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // 1) fulfill a future IMMEDIATELY with a value
  val valueFuture: Future[Int] = Future.successful(42)
  println(s"Value: ${valueFuture.value}")

  def fulfillImmediately[T](value: T): Future[T] = Future.successful(value)

  // 2) inSequence(fa, fb)

  // my implementation #1
  //  def inSequence[T, R](fa: Future[T], fb: Future[R]): Future[Unit] = {
  //    val unitFuture = Future.successful(())
  //
  //    for {
  //      _ <- fa.map(_ => ()).recoverWith(_ => unitFuture)
  //      _ <- fb.map(_ => ()).recoverWith(_ => unitFuture)
  //    } yield ()
  //  }

  // my implementation #2
  def inSequenceMy[A, B](fa: Future[A], fb: Future[B]): Future[B] = {
    for {
      _ <- fa.map(_ => ()).recoverWith(_ => Future.successful(()))
      b <- fb
    } yield b
  }

  // solution
  def inSequence[A, B](fa: Future[A], fb: Future[B]): Future[B] = fa.flatMap(_ => fb)

  // 3) first(fa, fb) => new future with the first value of the two futures

  // my implementation -> same
  def firstMy[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promise = Promise[T]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // solution
  def first[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promise = Promise[T]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // 4) last(fa, fb) => new future with the last value

  // my implementation
  def lastMy[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promise = Promise[T]
    val faResult = AtomicReference[Try[T]]
    val fbResult = AtomicReference[Try[T]]

    fa.onComplete { t =>
      faResult.set(t)
      if (fbResult.get() ne null) {
        promise.complete(t)
      }
    }
    fb.onComplete { t =>
      fbResult.set(t)
      if (faResult.get() ne null) {
        promise.complete(t)
      }
    }

    promise.future
  }

  // solution
  def last[T](fa: Future[T], fb: Future[T]): Future[T] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete

    val bothPromise = Promise[T]
    val lastPromise = Promise[T]

    //    def tryComplete(t: Try[T]): Unit = {
    //      try {
    //        bothPromise.complete(t)
    //      } catch {
    //        case _: IllegalStateException => lastPromise.complete(t)
    //      }
    //    }

    def tryComplete(result: Try[T]): Unit = {
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)
    }

    fa.onComplete(tryComplete)
    fb.onComplete(tryComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }
  val slow = Future {
    Thread.sleep(200)
    45
  }
  first(fast, slow).foreach(f => println("FIRST: " + f))
  last(fast, slow).foreach(l => println("LAST: " + l))

  Thread.sleep(1_000)
  println("-----------")

  // 5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }

  val random = new Random()
  val action = () => Future {
    val nextValue = random.nextInt(100)
    println(s"Generated $nextValue")
    Thread.sleep(100)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => {
    println(s"Settled at $result")
    ex.shutdownNow()
  })
  Thread.sleep(10_000)
}
