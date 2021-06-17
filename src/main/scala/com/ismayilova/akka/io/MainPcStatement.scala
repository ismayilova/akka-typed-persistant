package com.ismayilova.akka.io

import java.time.Instant

import akka.actor.typed.ActorSystem
import com.ismayilova.akka.io.persistance.part2_event_sourcing.PcStatement
import com.ismayilova.akka.io.persistance.part2_event_sourcing.PcStatement.{GetQuery, Print}

object MainPcStatement  extends App {
  val ts = ActorSystem(PcStatement(),"PC1")
//  ts ! Print
  ts ! GetQuery("524375LKJNBB3099", Instant.parse("2021-05-01T00:00:00Z"),
    Instant.parse("2021-05-20T00:00:00Z"),
    5, 1621138229337L)

  ts ! Print

}
