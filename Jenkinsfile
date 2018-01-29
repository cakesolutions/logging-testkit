pipeline {
  agent {
    label 'sbt-slave'
  }

  stages {
    stage('Cross Scala Build Pipeline') {
      steps {
        ansiColor('xterm') {
          script {
            // The following values need to be synced with those in build.sbt and CommonProject.scala
            def crossScalaVersions = ["2.11.12", "2.12.4"]
            def builds = [:]
            crossScalaVersions.each { SCALA_VERSION ->
              builds[SCALA_VERSION] = {
                sh "sbt ++$SCALA_VERSION clean compile test:compile doc"
                sh "sbt ++$SCALA_VERSION coverage test"
                sh "sbt ++$SCALA_VERSION coverageReport"
                sh "sbt ++$SCALA_VERSION coverageAggregate"
              }
            }
            parallel builds
          }
        }
      }
    }
  }
}
