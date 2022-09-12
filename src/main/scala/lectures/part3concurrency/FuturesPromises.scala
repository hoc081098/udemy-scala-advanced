package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

import scala.concurrent.*
import scala.util.Success
import scala.util.Failure

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
}