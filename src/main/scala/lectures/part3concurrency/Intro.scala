package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
  /*
    interface Runnable {
      public void run();
    }
   */

  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  // JVM threads
  val aThread = new Thread(runnable)

  aThread.start() // give a signal to the JVM to start a JVM thread
  // create a JVM thread => OS thread
  runnable.run() // doesn't do anything in parallel!
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))

  threadHello.start()
  threadGoodbye.start()
  // different runs produce different results!

  // executors
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("something in the thread pool"))
  pool.execute(() => {
    Thread.sleep(1_000)
    println("done after 1 second")
  })
  pool.execute(() => {
    Thread.sleep(1_000)
    println("almost done")
    Thread.sleep(1_000)
    println("done after 2 seconds")
  })
  pool.shutdown() // does not accept any more actions
  // pool.execute(() => println("should not appear")) // throws an exception in the calling thread
  // pool.shutdownNow()
  println(pool.isShutdown) // true
}
