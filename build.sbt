name := "ctp-challenge-server"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.12"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,

    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test"
  )
}