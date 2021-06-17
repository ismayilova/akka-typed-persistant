package comismayilova.akka.io.oracleDb

import akka.actor.typed.ActorSystem
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import com.ismayilova.akka.io.persistance.part2_event_sourcing.PcStatement
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}

object SlickAlpakka  extends App{

//  val system  = ActorSystem(PcStatement(),"PC1")
//  val ec: ExecutionContext = system.executionContext
  val databaseConfig =
    DatabaseConfig.forConfig[JdbcProfile]("slick-oracle")

  implicit val session: SlickSession = SlickSession.forConfig(databaseConfig)

//slick

  val payload = "524375WXRNVO8495"
  import session.profile.api._
  println("BEGIN ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
   val res = Slick
      .source(sql"""
          select RMS_HELPER.getCustomerIdByPseudoPan($payload) from dual
        """.as[String])
println(res)

println("CLOSING --------------------------------------------------------------------------------")
   session.close()
  println("END=======================================================================================================")
}
