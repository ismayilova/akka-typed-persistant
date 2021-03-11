package com.ismayilova.akka.io.persistance.part2_event_sourcing

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.persistence.typed.scaladsl.EventSourcedBehavior.CommandHandler

object ActorContextPersistant {

  sealed trait Command
  case object Hello extends Command
  case object Bye extends Command


  case class State(data: List[String])


  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      EventSourcedBehavior[Command, String, State](
        persistenceId = PersistenceId.ofUniqueId("myPersistenceId"),
        emptyState = State(Nil),
        commandHandler = CommandHandler.command { cmd =>
          println("UPPPPERRRRRRRRRRR IN COMMAND HANDLER")
          context.log.info("Got command {}", cmd)

          Effect.none
        },
        eventHandler = {
          case (state, _) => state
        })


    }



}