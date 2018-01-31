// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker

import java.io.InputStream

import scala.concurrent.duration._
import scala.io.Source
import scala.sys.process.Process

import io.circe.Json
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Notification
import monix.reactive.Notification.OnError
import org.scalatest.{FreeSpec, Matchers}
import net.cakesolutions.testkit.logging.LogEvent
import net.cakesolutions.testkit.logging.docker.formats.LogEventFormat
import net.cakesolutions.testkit.logging.docker.DockerLogSource.ProcessTerminated
import net.cakesolutions.testkit.matchers.ObservableMatcher._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DockerLogSourceTest extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {

  implicit val timeout: FiniteDuration = 3.seconds

  "Normal exit" in {
    val sampleLogStream: InputStream = getClass.getResourceAsStream("/sample-docker-logging.txt")
    val logLines = Source.fromInputStream(sampleLogStream).getLines.toSeq
    val decoder: LogEventFormat = new LogEventFormat("test")
    val expectedLogLines = logLines.map(decoder.parse).collect {
      case Some(Right(event)) =>
        event
    }
    val testLogSource = new TestDockerLogSource(0, logLines: _*)

    testLogSource.source("test") should observe[LogEvent[Json]](expectedLogLines: _*)
  }

  "Abnormal exit" in {
    val exitCode = 1
    val testLogSource = new TestDockerLogSource(exitCode)

    testLogSource.source("test").materialize should observe[Notification[LogEvent[Json]]](OnError(ProcessTerminated(exitCode)))
  }
}

class TestDockerLogSource(exitCode: Int, logLine: String*) extends BaseDockerLogSource {
  override def pollingProcess(id: String, handler: String => Unit): Process = {
    new Process {
      override def exitValue(): Int = {
        logLine.foreach(handler)
        exitCode
      }

      override def destroy(): Unit = {}
    }
  }
}
