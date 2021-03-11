package com.ismayilova.akka.io.persistance.part2_event_sourcing

import akka.actor.ActorLogging
import akka.actor.typed.Behavior
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.Effect
object PersistantActors {

  sealed trait Command
  final case class Add(data: String) extends Command
  case object Clear extends Command
  case object Print extends Command


  sealed trait Event
  final case class  Added(data:String) extends Event
  case object Cleared extends Event


  case class State(history:List[String] = Nil)  /*State is a List containing the 5 last items*/


  // Example factoring out a chained effect to use in several places with `thenRun`
  val commonChainedEffects: State => Unit = _ => println(s" Then  run Command processed = ADDED")

  val commonChainedEffects2: String => Unit = s => println(s" Then  run Command processed = ADDED $s")
  val commandHandler:(State , Command)=>Effect[Event,State] = {(state , command)=>

    command match {
      case Print => println(state.history)
        Effect.none
      case Add(data) =>
       println(s"Add($data)")
       // Effect.none
       Effect.persist(Added(data)).thenRun(commonChainedEffects)
      case Clear =>
        println(s"Clear")
       Effect.persist(Cleared)
      //  Effect.none
    }
  }



  val eventHandler : (State , Event) =>State  = {(state , event)=>
    event match {
      case Added(data) =>state.copy((data :: state.history).take(5))
      case Cleared =>State(Nil)
    }
  }





  def apply():Behavior[Command] =EventSourcedBehavior[Command ,Event,State](
    persistenceId = PersistenceId.ofUniqueId("abc"),
    emptyState = State(Nil),
    commandHandler = commandHandler,
    eventHandler = eventHandler
  )


}




