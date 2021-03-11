package com.ismayilova.akka.io.persistance.part2_event_sourcing

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}

import scala.collection.mutable


object Exercise {

  sealed trait Command
  case class Vote(citizenId:String , candidates:String) extends Command
  case object Print extends Command
 case class CantVote(citizenId:String) extends Command

  sealed trait Event
  case class Voted(citizenId:String , candidates:String) extends Event
  case object DefaultEvent extends Event
  case class Failured(citizenID:String) extends Event


  case class State(voted:Map[String , String])
  val poll:scala.collection.mutable.Map[String,Int] = new mutable.HashMap[String,Int]()   // candidates ->int amount of votes
  val votedCitizens:scala.collection.mutable.Set[String] = new mutable.HashSet[String]()





  val commandHandler:(State , Command)=>Effect[Event,State] = {(state , command)=>

    command match {
            case Vote(citizenId , candidates) => println(state.voted + s"  [$citizenId] voted for [$candidates]")
              if(!votedCitizens.contains(citizenId))
                Effect.persist(Voted(citizenId , candidates))
              else Effect.persist(Failured(citizenId))
            case Print => println(s"Voted state :" + state.voted)
                         println(s"Voted citizens : [ $votedCitizens]")
                          println(s"Candidates Results [ $poll]")
              Effect.none

    }
  }



//  val commandHandler: (State , Command)=>Effect[Event,State] = { (state , command)=>
//
//   // println("START LOOK MA")
//    command match {
//      case Vote(citizenId , candidates) => println(s"[$citizenId] voted for [$candidates]")
//
//        //Effect.persist(Voted(citizenId,candidates))
//       Effect.none
//      case _=> println("Ddefault Commannd")
//         Effect.none
//    }
//  }
  val eventHandler: (State , Event)=>State = (state,event)=>{
    event match {
      case Voted(citizenId, candidates) =>
        println(s"$citizenId -> $candidates")
        votedCitizens.add(citizenId)
        val votes:Int = poll.getOrElse(candidates , 0)
        poll.put(candidates , votes+1)

        state.copy( state.voted + (citizenId->candidates))


      case Failured(citizenID) => println(s"[$citizenID] has already voted")
         state
      case _ =>state
    }
  }



  def apply():Behavior[Command] =EventSourcedBehavior[Command ,Event,State](
    persistenceId = PersistenceId.ofUniqueId("kkk"),
    emptyState = State(Map()),
    commandHandler = commandHandler,
    eventHandler = eventHandler
  )




}
