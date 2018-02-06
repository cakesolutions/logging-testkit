// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.docker

import scala.sys.process.Process

class TestDockerLogSource(exitCode: Int, logLine: String*) extends DockerLogLineSource {
  override def pollingProcess(handler: String => Unit): Process = {
    new Process {
      private[this] var isAlive: Boolean = true

      override def exitValue(): Int = {
        logLine.foreach(handler)
        isAlive = false
        exitCode
      }

      override def destroy(): Unit = {}

      override def isAlive(): Boolean = {
        isAlive
      }
    }
  }
}
