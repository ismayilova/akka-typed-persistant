package com.ismayilova.akka.io.oracleDb

import org.springframework.jdbc.core.JdbcTemplate
import java.sql.{CallableStatement, Connection, DriverManager, ResultSet, SQLException}

import oracle.jdbc.OracleTypes

object MainDB extends App{

  println(checkMBSessionKey("S","D").getOrElse("SID"))
  println(getClientIdByPseudoPan("524375WXRNVO8495").getOrElse("SID"))
  private def ConnectToOracleDB: Option[Connection] = {
    try {
      Some(
        DriverManager.getConnection(
          "jdbc:oracle:thin:@testabs:1521:TESTABS",
          "RMS_PROXY",
          "RMS_PROXY"
        )
      )
    } catch {
      case e: Throwable =>
        e.getMessage
        None
    }
  }

  def checkMBSessionKey (sessionKey :String, deviceId:String) :Option[String] = {

    val connect = ConnectToOracleDB

    connect match {
      case conn :Some[Connection] =>
        val connection = conn.get
        val DB_Query = connection.prepareCall(
          "begin ? := RMS_HELPER.getCustomerIdByMBSessionKey(?,?); end;"
        )
        DB_Query.registerOutParameter(1, OracleTypes.VARCHAR)
        DB_Query.setString(2, sessionKey)
        DB_Query.setString(3, deviceId)
        DB_Query.execute
        val result = DB_Query.getString(1)
        DB_Query.close()
        Some(result)

      case None => None
    }

  }
  def getClientIdByPseudoPan(pseudoPan:String): Option[String] = {

//    val connect = ConnectToOracleDB
//
//    connect match {
//      case conn :Some[Connection] =>
//        val connection = conn.get
//        val DB_Query = connection.prepareCall(
//          "begin ? := RMS_HELPER.getCustomerIdByPseudoPan(?); end;"
//        )
//        DB_Query.registerOutParameter(1, OracleTypes.VARCHAR)
//        DB_Query.setString(2, pseudoPan)
//
//        DB_Query.execute
//        val result = DB_Query.getString(1)
//        DB_Query.close()
//        Some(result)
//
//      case None => None
//    }

    return Some("rr")
  }
}
