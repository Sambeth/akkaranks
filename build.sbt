ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.7"

lazy val akkaVersion = "2.6.18"

lazy val root = (project in file("."))
  .settings(
    name := "akkaranks",
    idePackagePrefix := Some("com.sambeth.akkaranks"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    )
  )
