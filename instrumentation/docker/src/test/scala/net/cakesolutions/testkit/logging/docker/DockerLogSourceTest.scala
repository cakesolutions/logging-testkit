// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker

import java.io.InputStream

import scala.concurrent.duration._
import scala.io.Source

import monix.execution.Scheduler.Implicits.global
import monix.reactive.Notification
import monix.reactive.Notification.OnError
import net.cakesolutions.testkit.matchers.ObservableMatcher._
import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DockerLogSourceTest extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {

  implicit val timeout: FiniteDuration = 3.seconds

  "Normal exit" in {
    val sampleLogStream: InputStream = getClass.getResourceAsStream("/sample-docker-logging.txt")
    val logLines = Source.fromInputStream(sampleLogStream).getLines.toSeq
    val testLogSource = new TestDockerLogSource(0, logLines: _*)

    testLogSource.source() should observe[String](logLines: _*)
  }

  "Abnormal exit" in {
    val exitCode = 42
    val testLogSource = new TestDockerLogSource(exitCode)

    testLogSource.source().materialize should observe[Notification[String]](OnError(ProcessTerminated(exitCode)))
  }
}
