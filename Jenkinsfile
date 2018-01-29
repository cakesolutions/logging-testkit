pipeline {
  agent {
    label 'sbt-slave'
  }

  stages {
    stage('Compile') {
      steps {
        ansiColor('xterm') {
          script {
            def crossScalaVersions = ["2.11.12", "2.12.4"]
            crossScalaVersions.each { SCALA_VERSION ->
              sh "sbt ++$SCALA_VERSION clean compile test:compile doc"
              sh "sbt ++$SCALA_VERSION coverage test"
              sh "sbt ++$SCALA_VERSION coverageReport"
              sh "sbt ++$SCALA_VERSION coverageAggregate"
            }
          }
        }
      }
    }
  }
}
