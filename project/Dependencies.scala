// Copyright 2018 Cake Solutions Limited
// Copyright 2016-2017 Carl Pulley

import sbt.Keys._
import sbt._

object Dependencies {
  object aws {
    private val version = "1.11.271"

    val logs: ModuleID = "com.amazonaws" % "aws-java-sdk-logs" % version
  }

  object circe {
    private val version = "0.9.1"

    val core: ModuleID = "io.circe" %% "circe-core" % version
    val generic: ModuleID = "io.circe" %% "circe-generic" % version
    val parser: ModuleID = "io.circe" %% "circe-parser" % version
  }

  val commonsLogging: ModuleID = "commons-logging" % "commons-logging" % "1.2"
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val logging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  object monix {
    private val version = "3.0.0-M3"

    val core: ModuleID = "io.monix" %% "monix" % version
    val reactive: ModuleID = "io.monix" %% "monix-reactive" % version
  }

  val scalacheck: ModuleID = "org.scalacheck" %% "scalacheck" % "1.13.5"
  val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
}
