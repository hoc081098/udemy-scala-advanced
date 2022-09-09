package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

object JVMConcurrencyProblems {
  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x) // race condition
  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    /*
      involves 3 steps:
      - read old value
      - compute result
      - write new value
     */
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.synchronized { // does not allow multiple threads to run the critical section AT THE SAME TIME
      bankAccount.amount -= price // critical section
    }
  }

  /*
    Example race condition:
    thread1 (shoes)
      - reads amount 50_000
      - compute result 50_000 - 3_000 = 47_000

    thread2 (iPhone)
      - reads amount 50_000
      - compute result 50_000 - 4_000 = 46_000

    thread1 (shoes)
      - write amount 47_000

    thread2 (iPhone)
      - write amount 46_000
   */
  def demoBankingProblem(): Unit = {
    (1 to 10_000).foreach { _ =>
      val account = BankAccount(50_000)

      val thread1 = new Thread(() => buySafe(account, "shoes", 3_000))
      val thread2 = new Thread(() => buySafe(account, "iPhone", 4_000))
      thread1.start()
      thread2.start()

      thread1.join()
      thread2.join()

      if (account.amount != 43_000) println(s"AHA! I've just broken the bank: ${account.amount}")
    }
  }

  /*
    Exercise
    1 - create "inception threads"
      thread1
        -> thread 2
          -> thread 3
            ...
      each thread prints "hello from thread $i"
      Print all messages IN REVERSE ORDER

    2 - what's the max/min value of x
    3 - "sleep fallacy": what's the value of message?
   */
  // 1 - inception threads
  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val next = inceptionThreads(maxThreads, i + 1)
        next.start()
        next.join()
      }

      println(s"hello from thread $i")
    })
  }

  // 2
  // max: 100 - each thread increases x by 1
  // min: 1
  //    all threads read x = 0 at the same time
  //    all threads (in parallel) compute 0 + 1 = 1
  //    all threads try to write x = 1
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start)
  }

  // 3
  // almost always, message = "Scala is awesome"
  // it is guaranteed? NO
  // Obnoxious situation (possible):
  //
  // main thread:
  //    message = "Scala is awesome"
  //    awesomeThread.start()
  //    sleep(1001) - yield execution
  // awesome thread
  //    sleep(1000) - yield execution
  // OS gives the the CPU to some import thread, takes > 2s
  // main thread:
  //    println(message) // "Scala sucks"
  // awesome thread:
  //    message = "Scala is awesome"
  //
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1_000)
      message = "Scala is awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1_001)

    // solution: join the worker thread
    awesomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
    demoBankingProblem()
    inceptionThreads(50).start()
    (0 until 5).foreach(_ => demoSleepFallacy())
  }
}
