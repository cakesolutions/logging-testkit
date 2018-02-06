// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging.docker

import scala.concurrent.{blocking, Promise}
import scala.sys.process.{Process, ProcessLogger}
import scala.util.control.NonFatal

import com.typesafe.scalalogging.Logger
import monix.execution.Scheduler
import monix.reactive.observers.Subscriber
import net.cakesolutions.testkit.config.Configuration.Logging
import net.cakesolutions.testkit.logging.LineLoggingSource
import net.cakesolutions.testkit.logging.docker.DockerLogLineSource.ProcessTerminated

trait DockerLogLineSource extends LineLoggingSource[String] {

  private val log = Logger(Logging.name)

  /**
    * Process for polling the Docker container for logging lines.
    *
    * @param handler handler that will manage each received log line
    * @return process for polling the Docker container for logging lines
    */
  protected def pollingProcess(handler: String => Unit): Process

  override protected def subscriberPolling(
    subscriber: Subscriber[String],
    cancelP: Promise[Unit]
  )(implicit
    scheduler: Scheduler
  ): Unit = {
    def handleLogEvent(event: String): Unit = {
      if (! cancelP.isCompleted) {
        try {
          subscriber.onNext(event)
        } catch {
          case NonFatal(exn) =>
            log.error("Unexpected exception sending data to a subscriber", exn)
        }
      }
    }

    blocking {
      val process = pollingProcess(handleLogEvent)

      cancelP.future.onComplete(_ => process.destroy())(scheduler)

      // 143 = 128 + SIGTERM
      val exit = process.exitValue()
      if (exit != 0 && exit != 143) {
        val cause = ProcessTerminated(exit)
        cancelP.failure(cause)
        subscriber.onError(cause)
        throw ProcessTerminated(exit)
      }
      if (! cancelP.isCompleted) {
        cancelP.success(())
        subscriber.onComplete()
      }
    }
  }
}

// $COVERAGE-OFF$
object DockerLogLineSource extends DockerLogLineSource {
  private val dockerLogsCmd =
    Seq(
      "docker-compose",
      "logs",
      "-f",
      "docker/docker-compose.yml",
      "-f",
      "docker/docker-compose-test.yml",
      "-f",
      "-t"
    )

  /**
    * Error signal indicating that a process has unexpectedly terminated.
    *
    * @param exitCode exit code the process terminated with
    */
  final case class ProcessTerminated(exitCode: Int) extends Exception(s"ProcessTerminated($exitCode)")

  /** @inheritdoc */
  override def pollingProcess(handler: String => Unit): Process = {
    Process(dockerLogsCmd).run(ProcessLogger(handler, handler))
  }
}
// $COVERAGE-ON$
