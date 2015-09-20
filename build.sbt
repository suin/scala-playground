name := """scala-playground"""

version := "1.0"

scalaVersion := "2.11.7"

// Akka actor libraries
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.14"
)

// Spray: A suite of scala libraries for building and consuming RESTful web services on top of Akka
libraryDependencies ++= Seq(
  "io.spray" %%  "spray-can" % "1.3.3",
  "io.spray" %%  "spray-routing" % "1.3.3",
  "io.spray" %% "spray-testkit" % "1.3.3" % "test"
)

// Date time libraries
libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.2",
  "org.joda" % "joda-convert" % "1.7"
)

// Test libraries
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
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

Revolver.settings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
