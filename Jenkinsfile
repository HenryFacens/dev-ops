pipeline {
    agent any

    options {
        timestamps()
        ansiColor('xterm')
    }

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_IMAGE       = "pedrogamerp/recommendation-api"
        DOCKER_COMPOSE_DEV = "docker-compose.yml"
        DOCKER_COMPOSE_STG = "docker-compose.staging.yml"
    }

    stages {
        // ============================================
        // PIPELINE DEV - Code Quality & Build
        // ============================================
        stage('🔍 DEV - Checkout') {
            steps {
                echo '=========================================='
                echo '       PIPELINE DEV - INICIANDO'
                echo '=========================================='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: false,
                          healthScaleFactor: 1.0
                    echo "✅ Testes unitários concluídos"
                }
            }
        }

        stage('Image_Docker - Build & Push') {
            environment {
                IMAGE_TAG = "${env.BUILD_NUMBER}"
            }
            steps {
                sh 'docker build -t recommendation-api .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}

