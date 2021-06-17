package com.ismayilova.akka.io.persistance.part2_event_sourcing

import java.util.Date
import java.time.{Clock, Instant, LocalDate}
import java.util.{Calendar, Random}

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}


object PcStatement {
  sealed trait Command
  case class GetQuery(pseudoPan: String, startDate:Instant,endDate:Instant,pageSize:Int , lastTransactionId: Long ) extends Command
  case object Print extends Command

  sealed trait Event





  val fakePseudoPan :String = "524375LKJNBB3099"


  case class PcOperation(
                          pseudoPan :String ,
                          transactionId :String ,
                          transactionRrn :String ,
                          transactionType :String ,
                          transactionTime :Instant ,
                          transactionAmount :Double ,
                          transactionCurrency :String ,
                          isReversal :Boolean ,
                          senderPseudoPan :String ,
                          senderPan :String ,
                          senderName :String ,
                          recipientPseudoPan :String ,
                          recipientPan :String ,
                          recipientName :String ,
                          acquirerCountry :String ,
                          vendorDescription :String ,
                          merchantTime :Instant ,
                          terminalId :String ,
                          terminalPurchaseCategory :String ,
                          terminalDescription :String ,
                          terminalCountry :String ,
                          operationType :String ,
                          accountNumber :String ,
                          accountCurrency :String ,
                          accountAmount :Double ,
                          availableBalance :Double ,
                          approvalCode :String ,
                          paymentByNFC :Boolean ,
                          purposeOfPayment :String ,
                          lifePhase :String ,
                          responseResult :String ,
                          linkKey :String ,
                          createdTimeStamp :Instant
                        )

  case class PcStatementState( operations: scala.collection.mutable.Seq[PcOperation]  )

   val pcOperation : PcOperation = PcOperation(
     pseudoPan  = fakePseudoPan,
     transactionId  = "1",
     transactionRrn  = "123456123456",
     transactionType  = "CASH",
     transactionTime  = Instant.parse("2021-01-01T00:00:00Z"),
  transactionAmount  = 100.00,
  transactionCurrency  = "AZN",
  isReversal  = false,
  senderPseudoPan  = fakePseudoPan,
  senderPan  = fakePseudoPan,
  senderName  = "senderName",
  recipientPseudoPan  = fakePseudoPan,
  recipientPan  = fakePseudoPan,
  recipientName  = "recipientName",
  acquirerCountry  = "AZ",
  vendorDescription  = "Cash operation",
  merchantTime  = Instant.parse("2021-01-01T00:00:00Z"),
  terminalId  = "2222083",
  terminalPurchaseCategory  = "ATM",
  terminalDescription  = "Description",
  terminalCountry  = "AZ",
  operationType  = "Operation Type",
  accountNumber  = "AZN001",
  accountCurrency  = "AZN",
  accountAmount  = 200.00,
  availableBalance  = 200.00,
  approvalCode  = "123456",
  paymentByNFC  = false,
  purposeOfPayment  = "Personal use",
  lifePhase  = "Single",
  responseResult  = "Approved",
  linkKey  = "linkKey",
  createdTimeStamp  = Instant.parse("2021-01-01T00:00:00Z")
      )

  def apply():Behavior[Command] =EventSourcedBehavior[Command ,Event,PcStatementState](
    persistenceId = PersistenceId.ofUniqueId("PC1"),
    emptyState = PcStatementState(createOperations(20) ),
    commandHandler = commandHandler,
    eventHandler = eventHandler

  )

  //Will create random date between given dates
  def randomDateBetween( firstDate : Date, secondDate : Date) : Instant =
  {
    val ratio = new Random().nextInt(100);

    val difference = (secondDate.getTime - firstDate.getTime)

    val surplusMillis = (difference * (ratio / 100.0)).asInstanceOf[Long]

    val cal = Calendar.getInstance()

    cal.setTimeInMillis(surplusMillis + firstDate.getTime)

    return cal.getTime().toInstant
  }

  //Will create initial states with given amount of operations
  def createOperations(size:Int):scala.collection.mutable.Seq[PcOperation]  = {
    var res:scala.collection.mutable.Seq[PcOperation] = scala.collection.mutable.Seq.empty
    val date1 = Calendar.getInstance
    val date2 = Calendar.getInstance

    date1.set(2021, 4, 1)
    date2.set(2021, 4, 20)



    for(i <-1 to size){
      val randomDate = randomDateBetween(date1.getTime() ,date2.getTime())
      res = res :+ pcOperation.copy(transactionId = randomDate.toEpochMilli.toString, transactionAmount = (i+100).toDouble , transactionTime = randomDate)
    }

    return res
  }


  val commandHandler:(PcStatementState,Command)=>Effect[Event,PcStatementState] ={
    (state,command)=>
      command match {
        case Print =>
                println("Just printing stuff ..........")

                state.operations.foreach{ x=>println(s"${x.transactionTime} & ${x.transactionId} ")}
                Effect.none
        case GetQuery( _,startDate, endDate, 0, 0L) =>
          val res = state.operations.view
            .filter(x =>
            x.transactionTime.compareTo(startDate) >= 0 && x
              .transactionTime
              .compareTo(endDate) <= 0).to(Seq)

            res.foreach{ x=>println(s"${x.transactionTime} & ${x.transactionId} ")}
            Effect.none
        case GetQuery( _,startDate, endDate, pageSize, 0L) =>
          val res = state.operations.view
             .filter(x =>
             x.transactionTime.compareTo(startDate) >= 0 && x
               .transactionTime
               .compareTo(endDate) <= 0).take(pageSize).to(Seq)

             res.foreach{ x=>println(s"${x.transactionTime} & ${x.transactionId} ")}
          Effect.none
        case GetQuery( _,startDate, endDate, pageSize, lastTransactionId) =>
         val res =  state
            .operations.view
            .filter(x =>
              x.transactionTime.compareTo(startDate) >= 0 && x
                .transactionTime
                .compareTo(endDate) <= 0
                &&
                x.transactionId.toLong < lastTransactionId
            )
           .take(pageSize).to(Seq)

           res.foreach{ x=>println(s"${x.transactionTime} and ${x.transactionId} ")}
          Effect.none

        case _=>Effect.none
      }
  }



  val eventHandler:( PcStatementState ,  Event)=>PcStatementState ={
    (state,event) =>
      event match {
        case _ =>state
      }
  }

}
