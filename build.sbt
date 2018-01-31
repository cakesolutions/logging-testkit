// Copyright 2018 Cake Solutions Limited

import Dependencies._

enablePlugins(GitBranchPrompt)
enablePlugins(GitVersioning)
enablePlugins(SiteScaladocPlugin)

// The following values need to be synced with those in Jenkinsfile
crossScalaVersions := Seq("2.11.12", "2.12.4")
scalaVersion := crossScalaVersions.value.head

Publish.settings

git.useGitDescribe := true
ivyLoggingLevel := UpdateLogging.Quiet

lazy val core = project.in(file("core"))
  .settings(CommonProject.settings)
  .settings(ScalaDoc.settings)
  .settings(
    name := "logging-testkit",
    libraryDependencies ++= Seq(
      circe.core,
      circe.generic,
      circe.parser,
      logback,
      logging,
      monix.core,
      monix.reactive,
      scalatest % Test,
      scalacheck % Test
    ),
    coverageMinimum := 75
  )

lazy val docker = project.in(file("instrumentation/docker"))
  .dependsOn(core % "compile->compile; test->test")
  .settings(CommonProject.settings)
  .settings(
    name := "logging-testkit-docker",
    coverageMinimum := 70
  )

lazy val elasticsearch = project.in(file("instrumentation/elasticsearch"))
  .dependsOn(core % "compile->compile; test->test")
  .settings(CommonProject.settings)
  .settings(
    name := "logging-testkit-elasticsearch",
    libraryDependencies += aws.logs,
    coverageMinimum := 100
  )
