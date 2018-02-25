// Copyright 2018 Cake Solutions Limited
// Copyright 2016-2017 Carl Pulley

import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.{coverageExcludedFiles, coverageFailOnMinimum, coverageMinimum}

/**
  * Common project settings
  */
object CommonProject {
  val settings =
    Seq(
      organization := "net.cakesolutions",
      // The following values need to be synced with those in Jenkinsfile
      crossScalaVersions := Seq("2.11.12", "2.12.4"),
      scalaVersion := crossScalaVersions.value.head,
      scalacOptions in Compile ++= Seq(
        "-encoding", "UTF-8",
        "-feature",
        "-deprecation",
        "-unchecked",
        "-language:postfixOps",
        "-language:implicitConversions",
        "-Xlint",
        "-Yno-adapted-args",
        "-Ywarn-dead-code",
        "-Ywarn-numeric-widen",
        "-Xfuture",
        "-Ywarn-unused-import",
        "-Ypartial-unification",
        "-Xfatal-warnings"
      ),
      // Disable unused import warnings in Scala console.
      scalacOptions in (Compile, console) -= "-Ywarn-unused-import",
      scalacOptions in (Compile, doc) ++= {
        val nm = (name in(Compile, doc)).value
        val ver = (version in(Compile, doc)).value

        DefaultOptions.scaladoc(nm, ver)
      },
      javacOptions in (Compile, compile) ++= Seq(
        "-source", "1.8",
        "-target", "1.8",
        "-Xlint:all",
        "-Xlint:-options",
        "-Xlint:-path",
        "-Xlint:-processing",
        "-Werror"
      ),
      javacOptions in doc := Seq(),
      javaOptions += "-Xmx2G",
      outputStrategy := Some(StdoutOutput),
      testOptions in Test += Tests.Argument("-oFD"),
      fork := true,
      fork in test := true,
      coverageMinimum := 0,
      coverageFailOnMinimum := true,
      coverageExcludedFiles := ".*/target/.*",
      dependencyOverrides += Dependencies.commonsLogging
    )
}
