package com.hoc081098.udemyscalaadvanced
package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {
  /*
    the producer-consumer problem

    producer -> [ x ] -> consumer
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int) = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = SimpleContainer()

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while (container.isEmpty) {
        println("[consumer] actively waiting...")
      }

      println(s"[consumer] I have consumed ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)

      val value = 42
      println(s"[producer] I have produced, after long work, the value is $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //   naiveProdCons()

  // wait-and-notify
  // synchronized
  // entering a synchronized expression on an object "locks the object"
  // only AnyRefs can have synchronized blocks

  // wait()-ing on an object's monitor suspends you (the thread) indefinitely
  // waiting and notifying only work in "synchronized expressions"

  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }

      // container must have some value
      println(s"[consumer] I have consumed ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work...")
      Thread.sleep(2_000)

      val value = 42
      container.synchronized {
        println(s"[producer] I'm producing $value")

        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  // smartProdCons()

  /*
    producer -> [ ? ? ? ] -> consumer
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer] consumed $x")

          // hey producer, there's empty space available, are you lazy?!
          buffer.notify()
        }

        Thread.sleep(random.nextInt(250))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer] producing ${i}")
          buffer.enqueue(i)

          // hey consumer, new food for you
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  prodConsLargeBuffer()
}
