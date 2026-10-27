
pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        IMAGE_NAME = 'campus-equipment-booking'
        IMAGE_TAG  = 'set-a'
        JAR_FILE   = 'target/campus-equipment-booking-1.0.0.jar'
    }

    options {
        timestamps()
        skipDefaultCheckout(true)
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm

                echo 'Source code checked out successfully.'

                bat 'git branch --show-current'
                bat 'git log -1 --oneline'
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker --version'
            }
        }

        stage('Clean') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }

            post {
                always {
                    junit(
                        testResults: 'target/surefire-reports/*.xml',
                        allowEmptyResults: false
                    )
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'

                script {
                    if (!fileExists(env.JAR_FILE)) {
                        error "Executable JAR not found: ${env.JAR_FILE}"
                    }
                }

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

    }

    post {
        success {
            echo 'SUCCESS: Maven tests, packaging and Docker image build completed.'
        }

        failure {
            echo 'FAILURE: Inspect the failed stage and console output.'
        }

        always {
            echo 'Jenkins pipeline execution finished.'
        }
    }
}
