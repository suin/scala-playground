// Code beauty plugins
resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.0")

// Testing plugins
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.1.0")

// Display your SBT project's dependency updates.
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")

// An SBT plugin for dangerously fast development turnaround in Scala
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")
