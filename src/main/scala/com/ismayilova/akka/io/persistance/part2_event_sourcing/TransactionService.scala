package com.ismayilova.akka.io.persistance.part2_event_sourcing

import java.time.{Instant, LocalDate}

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}

import scala.collection.mutable


object TransactionService {
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
  case class CommitTransaction(transactionId:String) extends Command
  case class CompleteCommitTransaction(transactionId:String) extends Command

  case class RollbackTransaction(transactionId:String) extends Command
  case class CompleteRollbackTransaction(transactionId:String) extends Command

  case object Print extends Command
  case object PrintAccounts extends  Command

  sealed trait Event
  case class  TransactionCommitted(halfEntry: HalfEntry) extends Event
  case class  CompletedCommitTransaction(halfEntry: HalfEntry) extends Event


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

  def halfEntryFinder( command: Command, state: TransactionState): Option[HalfEntry] = {
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
    persistenceId = PersistenceId.ofUniqueId("T2"),
    emptyState = TransactionState("transaction1","operation1","TBD","CREATED",LocalDate.now()
      ,Instant.now(),createhalfEntries() ),
    commandHandler = commandHandler,
    eventHandler = eventHandler

  )

  private def getUniqueAccountsId(halfEntries: scala.collection.mutable.Seq[HalfEntry]):mutable.Set[String] = {

    var accountsIdSet = scala.collection.mutable.Set[String]()
    for(halfEntry <-halfEntries) accountsIdSet.add(halfEntry.accountId)

    return accountsIdSet

  }
  val commandHandler:(TransactionState,Command)=>Effect[Event,TransactionState] ={
    (state,command)=>
      command match {
        case Print =>

          for(halfEntry<-state.halfEntries)
            println(s"halfEntry = ${halfEntry.orderInTransaction} ,halfentryStatus = [${halfEntry.halfEntryStatus}]  and halfEntryAccount = [${halfEntry.accountId}] And type = [${halfEntry.halfEntryType}]")
          Effect.none
        case PrintAccounts =>
          for(accountId <- getUniqueAccountsId(state.halfEntries ))
            println(s"$accountId")
          Effect.none

        case command: CommitTransaction =>
          val halfEntry = halfEntryFinder(command,state)
          halfEntry match {
            case Some(value) =>
              val event = TransactionCommitted(value)
              Effect.persist(event)
                //.thenRun( _ => commandHandler(state,CompleteCommitTransaction(command.transactionId)))

            case None =>Effect.none
             // commandHandler(state , CompleteCommitTransaction(command.transactionId))
           // Effect.none
          }

         // commitTransaction(command,state)
        case command: CompleteCommitTransaction =>
          val halfEntry = halfEntryFinder(command,state)
          halfEntry match {
            case Some(value) =>
              val event = CompletedCommitTransaction(value)
              Effect.persist(event)
                .thenRun(_ => commandHandler(state,CommitTransaction(command.transactionId)))

            case None =>Effect.none
          }
         // completeCommitTransaction(command, state)
        case command: RollbackTransaction =>Effect.none
         // rollbackTransaction(command, state)
        case command: CompleteRollbackTransaction => Effect.none
        //  completeRollbackTransaction(command, state)
        case _=>Effect.none
      }
  }




  def commitTransaction(command: Command,state: TransactionState) ={

    val halfEntry = halfEntryFinder(command,state)
    halfEntry match {
   case Some(value) =>
     val event = TransactionCommitted(value)
     Effect.persist(event)

   case None =>Effect.none
}
  }

  def completeCommitTransaction(command: Command,state: TransactionState) ={

    val halfEntry = halfEntryFinder(command,state)
    halfEntry match {
      case Some(value) =>
        val event = CompletedCommitTransaction(value)
        Effect.persist(event)

      case None =>Effect.none
    }
  }

  val eventHandler:( TransactionState ,  Event)=>TransactionState ={
    (state,event) =>
      event match {
        case evt:TransactionCommitted =>
          state.copy(halfEntries = state.halfEntries.map{
            hlf =>
              hlf match {
                case evt.halfEntry => hlf.copy(halfEntryStatus = "AwaitingReplyReleaseTransaction")
                case _ =>hlf
              }
          })

        case evt: CompletedCommitTransaction => state.copy(halfEntries = state.halfEntries.map{
          hlf => hlf match {
            case evt.halfEntry =>hlf.copy(halfEntryStatus = "RepliedReleaseTransaction")
            case _ =>hlf
          }
        })
        case _ =>state
      }
  }








}
