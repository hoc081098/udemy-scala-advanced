package com.hoc081098.udemyscalaadvanced
package lectures.part5ts

object SelfTypes extends App {
  // requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    this: Instrumentalist => // SELF TYPE whoever implements Singer to implement Instrumentalist

    // rest of the implementation or API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  //  // illegal
  //  class Vocalist extends Singer {
  //    override def sing(): Unit = ???
  //  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A

  class B extends A // B IS AN A

  trait T

  trait S {
    self: T => // S REQUIRES a T
  }

  // CAKE PATTERN => "dependency injection"

  class Component {
    // API
  }

  // DI
  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent {
    self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }

  trait ScalaApplication {
    self: ScalaDependentComponent with ScalaComponent =>
    //
  }

  // layer1 - small components
  trait Picture extends ScalaComponent

  trait Stats extends ScalaComponent

  // layer2 = compose
  trait Profile extends ScalaDependentComponent with Picture

  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  // cyclical dependencies
  //  class X extends Y
  //  class Y extends X

  trait X {
    self: Y =>
  }

  trait Y {
    self: X =>
  }

  class XY extends X with Y {

  }
}
