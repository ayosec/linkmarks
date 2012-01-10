
import AssemblyKeys._

name := "linkmarks"

version := "0.1"

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-deprecation", "-optimise", "-unchecked")

mainClass := Some("com.ayosec.linkmarks.Runner")

fork in run := true

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "1.2.1",
  "com.ning" % "async-http-client" % "1.6.5",
  "org.slf4j" % "slf4j-nop" % "1.6.2",
  "net.liftweb" %% "lift-json" % "2.4-M4",
  "org.jsoup" % "jsoup" % "1.6.1",
  "commons-io" % "commons-io" % "2.1",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

// Deps for Neo4J
// Exclude the neo4j-udc to disable the login
// http://docs.neo4j.org/chunked/snapshot/usage-data-collector.html#_how_to_disable_udc
libraryDependencies += "org.neo4j" % "neo4j-advanced" % "1.5" exclude("org.neo4j", "neo4j-udc")

seq(assemblySettings: _*)

assembleArtifact in packageScala := false

// vim: syntax=scala
