name := "udemy_remoting_cluster"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.6.10"
lazy val leveldbVersion = "0.7"
lazy val leveldbjniVersion = "1.8"
lazy val postgresVersion = "42.2.2"
lazy val cassandraVersion = "0.91"
lazy val json4sVersion = "3.2.11"
lazy val protobufVersion = "3.6.1"

val SlickVersion = "3.3.2"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,

//  local levelDB stores
  "org.iq80.leveldb" % "leveldb" % leveldbVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniVersion,


//  JDBC with PostgreSQL
//  "org.postgresql" % "postgresql" % postgresVersion ,
//   "com.github.dnvriend" %% "akka-persistance-jdbc" % "3.4.0" ,


  //Cassandra
//  "com.typesafe.akka" %% "akka-persistance-cassandra" % cassandraVersion,
//  "com.typesafe.akka" %% "akka-persistance-cassandra-launcher" % cassandraVersion % Test,

  // Google Protocol Buffers
  "com.google.protobuf" % "protobuf-java" % protobufVersion ,

  //ORACLE
  "com.lightbend.akka" %% "akka-persistence-jdbc" % "4.0.0",
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
  "com.oracle.database.jdbc" % "ojdbc8" % "21.1.0.0",


  //SPRING
  "org.springframework.boot" % "spring-boot-starter-data-jdbc" % "2.5.0",

  //SLICK

//  "com.typesafe.slick" %% "slick-extensions" % SlickVersion,

"com.lightbend.akka" %% "akka-stream-alpakka-slick" % "2.0.2",
"com.typesafe.akka" %% "akka-stream" % AkkaVersion
)











