package com.ismayilova.akka.io.persistance.part2_event_sourcing

import java.time.{Instant, LocalDate}
import java.util.UUID

import akka.actor.ActorLogging
import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import com.ismayilova.akka.io.persistance.part2_event_sourcing.Exercise.{Command, Event, State}
import com.ismayilova.akka.io.persistance.part2_event_sourcing.UdemyPersistant.Command

import scala.collection.mutable

object UdemyPersistant {



  case class  TransactionState( transactionId: String ,//= UUID.fromString("transaction1"),
                                operationId: _root_.scala.Predef.String  ,  //= "",
                                operationType: String ,//= "TBD",
                                transactionStatus: String,// = "CREATED",
                                operationDate: LocalDate , // = LocalDate.now(),
                                creationTimestamp: Instant,// = Instant.now(),
                                halfEntries: scala.collection.mutable.Seq[HalfEntry]  //= scala.collection.mutable.Seq.empty[HalfEntry]

                              );
  case class HalfEntry(
                      var  orderInTransaction: _root_.scala.Int ,//= 0,
                      var  halfEntryType: String , //= "DEBIT",
                      var  accountId: String ,//= UUID.fromString("Account1"),
                      var   id:Int ,//= 1,
                      var   currencyAmount:Double ,//= 0.0,
                      var   localAmount: _root_.scala.Double,// = 0.0,
                      var    halfEntryStatus:String ,//= "Initial"

                      )



  sealed trait Command
  case  object CommitTransaction extends Command
  case  class CommitTransaction2(accountId:String ,transactionId:String) extends Command  //DONE
  case  class CommitTransaction3(accountId:String ,transactionId:String) extends Command  //
  case class RollbackTransaction(accountId:String , transactionId1:String) extends Command
  case class CompleteCommitTransaction(accountId:String , transactionId1:String) extends Command
  case class  CompleteRollbackTransaction(accountId:String , transactionId1:String) extends Command

  case object CommitForJ extends Command


  case class CommitTransaction(accountId:String , transactionId:String) extends Command


  case object Print extends Command
  case object PrintAccounts extends  Command

  sealed trait Event
  case class TransactionCommitted(halfEntry: HalfEntry) extends Event
  case class TransactionCommitted2(accountId:String,transactionId:String) extends Event   //Changed it   should consume account Id
  case class TransactionCommitted3(accountId:String,transactionId:String) extends Event   //Changed it   should consume account Id

  case class TransactionCommittedList(halfEntries:scala.collection.mutable.Seq[HalfEntry],transactionId:String) extends Event   //New

case class CommittedforJ(halfEntry: HalfEntry) extends Event

  case class TransactionRolledBack(halfEntry: HalfEntry , transactionId:String) extends Event  //Shoul change  halfEntry -> account Id
  case class CompletedRollbackTransaction(accountId:String , transactionId1:String) extends Event
  case class CompletedCommitTransaction(accountId:String , transactionId1:String) extends Event   //Should not it return halfEntry ???


  case class Failured(exception: String) extends Event

  def createhalfEntries():scala.collection.mutable.Seq[HalfEntry] = {
    var res:scala.collection.mutable.Seq[HalfEntry] = scala.collection.mutable.Seq.empty

    res  = res:+ HalfEntry( 0,
    "DEBIT",
     "Account1",
    1,
   0.0,
  0.0,
   "RepliedHoldHalfEntry")
    res  = res:+ HalfEntry( 0,
      "CREDIT",
      "Account2",
      2,
      0.0,
      0.0,
      "RepliedHoldHalfEntry")
    res = res :+HalfEntry( 1,
      "CREDIT",
      "Account1",
      2,
      0.0,
      0.0,
      "RepliedHoldHalfEntry")
    res = res :+HalfEntry( 1,
      "DEBIT",
      "Account3",
      2,
      0.0,
      0.0,
      "RepliedHoldHalfEntry")

    return res
  }

  private def getUniqueAccountsId(halfEntries: scala.collection.mutable.Seq[HalfEntry]):mutable.Set[String] = {

    var accountsIdSet = scala.collection.mutable.Set[String]()
    for(halfEntry <-halfEntries) accountsIdSet.add(halfEntry.accountId)

    return accountsIdSet

  }


  private def getUniqueAccountsId(halfEntries: scala.collection.mutable.Seq[HalfEntry] , halfEntryType:String):mutable.Set[String] = {

    var accountsIdSet = scala.collection.mutable.Set[String]()
    for(halfEntry <-halfEntries)
      if(halfEntry.halfEntryType ==halfEntryType)
        accountsIdSet.add(halfEntry.accountId)

    return accountsIdSet

  }

  private def getHalfEntriesByAccountId(accountId:String , state:TransactionState ):scala.collection.mutable.Seq[HalfEntry] ={
    var res:scala.collection.mutable.Seq[HalfEntry] = scala.collection.mutable.Seq.empty
     for(halfEntry<-state.halfEntries)
        if(halfEntry.accountId ==accountId) res :+ halfEntry   //Should use filter

    res
  }
  private def getHalfEntriesByAccountId(accountId:String , state:TransactionState ,halfEntryType:String  ):scala.collection.mutable.Seq[HalfEntry] ={
    var res:scala.collection.mutable.Seq[HalfEntry] = scala.collection.mutable.Seq.empty
    for(halfEntry<-state.halfEntries)
      if(halfEntry.accountId ==accountId &&  halfEntry.halfEntryType == halfEntryType) res :+ halfEntry   //Should use filter

    res
  }


  def halfEntryFinder(
                       command: Command,
                       state: TransactionState
                     ): Option[HalfEntry] = {
    command match {

      case cmd: CommitTransaction =>
        state
          .halfEntries
          .find(halfEntry => halfEntry.halfEntryStatus == "RepliedHoldHalfEntry" )
      case _: CompleteCommitTransaction =>
        state
          .halfEntries
          .find(halfEntry =>
            halfEntry.halfEntryStatus == "AwaitingReplyReleaseTransaction"
          )
      case _: RollbackTransaction =>
        state
          .halfEntries
          .find(halfEntry => halfEntry.halfEntryStatus == "RepliedHoldHalfEntry" )
      case _: CompleteRollbackTransaction =>
        state
          .halfEntries
          .find(halfEntry =>
            halfEntry.halfEntryStatus == "AwaitingReplyRejectTransaction"
          )
      case _ => ???
    }
  }






  def apply():Behavior[Command] =EventSourcedBehavior[Command ,Event,TransactionState](
    persistenceId = PersistenceId.ofUniqueId("transaction23"),
    emptyState = TransactionState("transaction1","operation1","TBD","CREATED",LocalDate.now()
    ,Instant.now(),createhalfEntries() ),
    commandHandler = commandHandler,
    eventHandler = eventHandler
  )

val newCommandHandler:(TransactionState,Command)=>Effect[Event,TransactionState] ={
  (state,cmd)=>
    cmd match {
      case CommitTransaction =>Effect.none
      case _=>Effect.none
    }
}








  val commandHandler : (TransactionState , Command) => Effect[Event,TransactionState] = {
    (state , cmd)=>
      var events:List[Event] = List()
    cmd match {
      case CommitTransaction =>
       // var events : scala.collection.immutable.Seq[Event]  = scala.collection.immutable.Seq.empty

        println(s"Starting CommitTranscaction for given state transactionId = [${state.transactionId}]")
        for(halfEntry<-state.halfEntries)  events = TransactionCommitted(halfEntry) :: events
      Effect.persist(events)
      //Effect.none
      case Print =>

          for(halfEntry<-state.halfEntries)
            println(s"halfEntry = ${halfEntry.orderInTransaction} ,halfentryStatus = [${halfEntry.halfEntryStatus}]  and halfEntryAccount = [${halfEntry.accountId}] And type = [${halfEntry.halfEntryType}]")
        Effect.none
      case PrintAccounts =>
         for(accountId <- getUniqueAccountsId(state.halfEntries ))
           println(s"$accountId")
        Effect.none
  /// MAIN !!!
      case CommitTransaction2(accountId , transactionId)=>

       var accounts = getUniqueAccountsId(state.halfEntries)
        for(account<-accounts)  events = TransactionCommitted2(accountId, state.transactionId)::events
        Effect.persist(events)



      //NEW COMMIT
      case CommitTransaction3(accountId, transactionId) =>
        var accounts = getUniqueAccountsId(state.halfEntries,"DEBIT")
        for(account<-accounts)  events = TransactionCommitted3(accountId, state.transactionId)::events
        accounts = getUniqueAccountsId(state.halfEntries,"CREDIT")
        for(account<-accounts)  events = TransactionCommitted2(accountId, state.transactionId)::events
        Effect.persist(events)


      case CommitForJ =>
        val events:List[Event] = List()
        for(halfEntry<-state.halfEntries)  CommittedforJ(halfEntry)::events

       // val events = state.halfEntries.flatMap(entry =>TransactionCommitted(entry))
        Effect.persist(events)


      //21-01-2021
      case CommitTransaction(accountId,transactionId)=>
        val halfEntry = halfEntryFinder(cmd, state)
            //for(halfEntry<-state.halfEntries.filter(entry=>entry.accountId==accountId)) events = TransactionCommitted(halfEntry)::events
        //val eventsList = TransactionCommittedList(state.halfEntries.filter(entry=>entry.accountId==accountId),transactionId)
          halfEntry match {
            case Some(value) =>
              val event =TransactionCommitted(value)
              Effect.persist(event)
          }

           // Effect.persist(eventsList)

      //21-01-2021


      case CompleteCommitTransaction(accountId, transactionId1) =>   //NOT HANDLED
        var accounts = getUniqueAccountsId(state.halfEntries)
        for(account<-accounts)  events = CompletedCommitTransaction(accountId, state.transactionId)::events
        Effect.persist(events)

      case RollbackTransaction(accountId , transactionId1) =>
        Effect.none
      case CompleteRollbackTransaction(accountId, transactionId1)=>
        Effect.none
      case  _=>
        Effect.none
    }

  }

  val eventHandler:( TransactionState ,  Event)=>TransactionState  = {
    (state , event) =>
    event match {
      case TransactionCommitted(halfEntry) =>
//        var id = halfEntry.id

        state.copy(halfEntries = state.halfEntries.map { hlf =>
//          if (hlf == halfEntry) {
//            hlf.copy(halfEntryStatus = "AwaitingReplyReleaseTransaction")
//          } else {
//            hlf
//          }

          hlf match {
            case halfEntry =>hlf.copy(halfEntryStatus = "AwaitingReplyRejectTransaction")
            case _=>  hlf
          }
        })
//          if(halfEntry.id ==id && halfEntry.halfEntryType =="DEBIT" && halfEntry.halfEntryStatus =="RepliedHoldDebit")
//            halfEntry.halfEntryStatus = "AwaitingReplyReleaseTransaction"
//            //ReleaseTransaction(accountId)
//          else if(halfEntry.id ==id && halfEntry.halfEntryType =="CREDIT" && halfEntry.halfEntryStatus =="RepliedHoldCredit")
//            halfEntry.halfEntryStatus = "AwaitingReplyReleaseTransaction"


//      state

      case TransactionCommitted2(accountId , transactionId) =>
      var halfEntries = getHalfEntriesByAccountId(accountId,state)
        for( entry <- halfEntries)
          if(entry.halfEntryType =="DEBIT" && entry.halfEntryStatus =="RepliedHoldDebit")
            entry.halfEntryStatus = "AwaitingReplyReleaseTransaction"
          //ReleaseTransaction(accountId)
          else if( entry.halfEntryType =="CREDIT" && entry.halfEntryStatus =="RepliedHoldCredit")
            entry.halfEntryStatus = "AwaitingReplyReleaseTransaction"
        state


      case TransactionCommitted3(accountId , transactionId) =>
        val halfEntries = getHalfEntriesByAccountId(accountId,state)
        var newHalfEntries:scala.collection.mutable.Seq[HalfEntry] = scala.collection.mutable.Seq.empty
        for(entry<-halfEntries) {
          //var index:Int = state.halfEntries.indexOf(entry)
          if(entry.halfEntryType =="DEBIT" && entry.halfEntryStatus =="RepliedHoldDebit")
            newHalfEntries =newHalfEntries:+HalfEntry(
              orderInTransaction = entry.orderInTransaction,
              halfEntryType = entry.halfEntryType,
              accountId = entry.accountId,
              id = entry.id,currencyAmount = entry.currencyAmount,
              localAmount = entry.localAmount,
              halfEntryStatus = "AwaitingReplyHalfEntry")
            //newHalfEntries = newHalfEntries:+state.halfEntries(index).copy(halfEntryStatus ="AwaitingReplyHalfEntry")
        }
          TransactionState(transactionId,
          state.operationId ,
          halfEntries =newHalfEntries,
          operationType = state.operationType,
          transactionStatus  = state.transactionStatus,
          operationDate = state.operationDate,
          creationTimestamp = state.creationTimestamp)


      case CompletedCommitTransaction(accountId, state.transactionId)  =>
        var halfEntries = getHalfEntriesByAccountId(accountId,state)
        for( entry <- halfEntries)
         entry.halfEntryStatus = "RepliedReleaseTransaction"
        state

      case TransactionRolledBack(halfEntry: HalfEntry ,transactionId:String) =>state

      case evt: CommittedforJ =>state.copy(halfEntries = state.halfEntries.map{
        hlf =>hlf match {
          case evt.halfEntry=> if (hlf.halfEntryStatus =="RepliedHoldHalfEntry")
                                     hlf.copy(halfEntryStatus = "AwaitingReplyReleaseTransaction")
                                  else hlf
          case _=>hlf
        }
      })

      case _ => state

    }

  }


}
