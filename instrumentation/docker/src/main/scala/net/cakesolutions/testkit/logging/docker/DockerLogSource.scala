// Copyright 2018 Cake Solutions Limited
// Copyright 2017 Carl Pulley

package net.cakesolutions.testkit.logging.docker

import scala.concurrent.{blocking, Promise}
import scala.sys.process.{Process, ProcessLogger}
import scala.util.control.NonFatal

import com.typesafe.scalalogging.Logger
import io.circe.Json
import monix.execution.Scheduler
import monix.reactive.observers.Subscriber

import net.cakesolutions.testkit.config.Configuration.Logging
import net.cakesolutions.testkit.logging.{LogEvent, LoggingSource}
import net.cakesolutions.testkit.logging.docker.DockerLogSource.ProcessTerminated
import net.cakesolutions.testkit.logging.docker.formats.LogEventFormat

trait BaseDockerLogSource extends LoggingSource[Json] {

  private val log = Logger(Logging.name)

  /**
    * Process for polling the Docker container for logging lines.
    *
    * @param id identifies the Docker container from which log events are consumed
    * @param handler handler that will manage each received log line
    * @return process for polling the Docker container for logging lines
    */
  protected def pollingProcess(id: String, handler: String => Unit): Process

  /**
    *  @see net.cakesolutions.testkit.logging.LoggingSource
    *
    *  @throws ProcessTerminated thrown when the underlying process exits unexpectedly
    */
  override protected def subscriberPolling(
    id: String,
    subscriber: Subscriber[LogEvent[Json]],
    cancelP: Promise[Unit]
  )(implicit
    scheduler: Scheduler
  ): Unit = {
    val decoder = new LogEventFormat(id)
    val handleLogEvent: String => Unit = { event =>
      if (! cancelP.isCompleted) {
        decoder.parse(event) match {
          case Some(Right(value: LogEvent[Json])) =>
            try {
              subscriber.onNext(value)
            } catch {
              case NonFatal(exn) =>
                log.error("Unexpected exception sending data to a subscriber", exn)
            }
          case Some(Left(exn)) =>
            subscriber.onError(exn)
          case None =>
            // Empty log entry - so nothing to do
        }
      }
    }

    blocking {
      val process = pollingProcess(id, handleLogEvent)

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

object DockerLogSource extends BaseDockerLogSource {
  private val dockerLogsCmd = Seq("docker", "logs", "-f", "-t")

  /**
    * Error signal indicating that a process has unexpectedly terminated.
    *
    * @param exitCode exit code the process terminated with
    */
  final case class ProcessTerminated(exitCode: Int) extends Exception(s"ProcessTerminated($exitCode)")

  /** @inheritdoc */
  override def pollingProcess(id: String, handler: String => Unit): Process = {
    Process(dockerLogsCmd :+ id).run(ProcessLogger(handler, handler))
  }
}
