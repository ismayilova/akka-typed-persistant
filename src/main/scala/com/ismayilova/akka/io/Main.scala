package com.ismayilova.akka.io

import java.time.{Instant, LocalDate}

import akka.actor.typed.ActorSystem
import com.ismayilova.akka.io.persistance.part2_event_sourcing.ActorContextPersistant.Hello
import com.ismayilova.akka.io.persistance.part2_event_sourcing.TransactionService
import com.ismayilova.akka.io.persistance.part2_event_sourcing.TransactionService.{CommitTransaction, CompleteCommitTransaction, Print, PrintAccounts}
//import com.ismayilova.akka.io.persistance.part2_event_sourcing.UdemyPersistant.{CommitForJ, CommitTransaction2, CommitTransaction3, CompleteCommitTransaction, PrintAccounts, createhalfEntries}
//import com.ismayilova.akka.io.persistance.part2_event_sourcing.Exercise.{Print, Vote}
//import com.ismayilova.akka.io.persistance.part2_event_sourcing.UdemyPersistant.{CommitTransaction,Print}
import com.ismayilova.akka.io.persistance.part2_event_sourcing.{ActorContextPersistant, Exercise, PersistantActors, UdemyPersistant}
//import com.ismayilova.akka.io.persistance.part2_event_sourcing.PersistantActors.{Add, Clear, Print}

object Main extends App{

  println("BEGIN")


//  val persistentActor1 = ActorSystem(PersistantActors(),"persistant1")
//
//  persistentActor1 ! Add("item1")
//  persistentActor1 ! Add("item2")
//  persistentActor1 ! Add("item3")
//  persistentActor1 ! Print
//
//
//println("before TT")
//  val tt = ActorSystem(ActorContextPersistant(),"context")
// println("tt")
//  tt ! Hello
//
//

/*
 val exercise = ActorSystem(Exercise(),"exercise3")
  exercise ! Vote("6FCH3S9TT","Candidate1")
  exercise ! Vote("4JU0YAQTT", "Candidate2")
  exercise ! Vote("333TT",  "Candidate3")
 exercise ! Vote("333TT",  "Candidate1")

  exercise ! Print
*/

//

// val exercise2 = ActorSystem(Exercise(),"exercise2")
// exercise2 ! Vote("6FCH3S9TT","Candidate1")
// exercise2 ! Vote("4JU0YAQTT", "Candidate2")
// exercise2 ! Vote("333TT",  "Candidate3")
// exercise2 ! Vote("333TT",  "Candidate1")
//
// exercise2 ! Print




 //=================================== TransactionService PERSISTATnt===================================
 val ts = ActorSystem(TransactionService(),"T2")



    ts ! PrintAccounts
    ts ! Print
    ts ! CommitTransaction("transaction1")
    ts ! Print
    ts ! CompleteCommitTransaction("transaction1")
   ts ! Print
 // TransactionServie ! Print

 //=================================== TransactionService PERSISTATnt===================================


 //=================================== UDEMY PERSISTATnt===================================
// val TransactionServie = ActorSystem(UdemyPersistant(),"transaction23")
//
//
//
//  TransactionServie ! PrintAccounts
//  TransactionServie ! Print
// //TransactionServie ! CommitTransaction("Account1","gg")
// TransactionServie ! CommitForJ
//  TransactionServie ! Print
//// TransactionServie ! Print


 //=================================== UDEMY PERSISTATnt===================================


//var res = UdemyPersistant.createhalfEntries()
//var state = UdemyPersistant.TransactionState("transaction1","operation1","TBD","CREATED",LocalDate.now()
// ,Instant.now(),res )
//
//val accountId:String = "Account3";
//  val newState = UdemyPersistant.TransactionState("transaction1","operation1","TBD","CREATED",LocalDate.now()
//    ,Instant.now(), state.halfEntries.filter(entry=>entry.accountId==accountId) )
//
//
//
//  for(entry<-newState.halfEntries)
//   println(s" [orderInTransaction]= ${entry.orderInTransaction} , [accountID]=${entry.accountId}, [halfEntryType]=${entry.halfEntryType}, [halfEntryStatus]=${entry.halfEntryStatus} ")



// def newState(state:UdemyPersistant.TransactionState):UdemyPersistant.TransactionState ={
//
//  for(entry<-state.halfEntries) {
//    var index: Int = state.halfEntries.indexOf(entry)
//    state.halfEntries(index).copy(halfEntryStatus = "AwaitingReplyHalfEntry")
//    println(state.halfEntries.indexOf(entry))
//  }
//  state
//
//
//  }

//var state2 = newState(state)
// for(entry<-state2.halfEntries)
//  println(s" [orderInTransaction]= ${entry.orderInTransaction} , [accountID]=${entry.accountId}, [halfEntryType]=${entry.halfEntryType}, [halfEntryStatus]=${entry.halfEntryStatus} ")



}
