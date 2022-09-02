package com.hoc081098.udemyscalaadvanced
package lectures.part2afp

object CurriesPAF extends App {
  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add4: Int => Int = curriedAdder(4)
  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA-EXPANSION
  List(1, 2, 3).map(x => inc(x)) // ETA-EXPANSION

  // Partial function applications
  val add5: Int => Int = curriedAdder(5) _ // SCALA3: _ is now unnecessary

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => y + 7
  // as many different implementations of add7 using above
  // be creative

  val add7_1 = (y: Int) => simpleAddFunction(y, 7) // simplest
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = simpleAddFunction(7, _: Int) // works as well

  val add7_4 = (y: Int) => simpleAddMethod(y, 7)
  val add7_5 = simpleAddMethod.curried(7)
  val add7_6 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values.
  // y => simpleAddMethod(7, y)

  val add7_7 = (y: Int) => curriedAddMethod(7)(y)
  val add7_8 = curriedAddMethod(7) // PAF
  val add7_9 = curriedAddMethod(7) _ // PAF
  val add7_10 = curriedAddMethod(7)(_) // PAF = alternative syntax

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?") // x: String => concatenator(hello, x, howareyout)
  println(insertName("hoc081098"))

  val filterInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("Hello, ", x, y)
  println(filterInTheBlanks("hoc081098", " Scala are awesome"))

  // EXERCISE
  /*
    1.  Process a list of numbers and return their string representations with different formats.
        Use the %4.2f, %8.6f and %14.12f with a curried formatter function

   */
  println("%8.6f".format(Math.PI))

  val curriedFormatter = (pattern: String) => (number: Double) => pattern.format(number)
  val numbers: List[Double] = List(1.2, 2.4323, 4343.34, Math.PI, 1, Math.E, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f")
  val seriousFormat = curriedFormatter("%8.6f")
  val preciseFormat = curriedFormatter("%14.12f")

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  /*
    2.  Different between
        - functions vs methods
        - parameters: by-name vs 0-lambda
  */
  def byName(n: => Int): Int = n + 1

  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
  calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
   */

  byName(23) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  // byName(parenMethod) // scala2 ok, scala 3 not ok
  // byName(() => 42) // not ok
  byName((() => 42) ()) // ok
  // byName(parenMethod _) // not ok

  // byFunction(45) // not ok
  // byFunction(method) // not ok!!!!!!!! does not do ETA-expansion!
  byFunction(parenMethod) // ok compiler does ETA-expansion!
  byFunction(() => 2) // ok, works as expected
  byFunction(parenMethod _) // ok, also works, unnecessary
}
