name := """scala-playground"""

version := "1.0"

scalaVersion := "2.11.7"

// Akka actor libraries
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.12"
)

// Test libraries
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:higherKinds",
  "-language:postfixOps",
  "-unchecked",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

// Scalariform settings
import scalariform.formatter.preferences._

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
