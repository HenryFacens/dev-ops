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
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('DEV - Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Image_Docker - Build & Push') {
            environment {
                IMAGE_TAG = "${env.BUILD_NUMBER}"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh '''
                        docker login -u "$DOCKER_USER" -p "$DOCKER_PASS"
                        docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Staging - Deploy') {
            steps {
                sh """
                    docker compose -f ${DOCKER_COMPOSE_STG} pull || true
                    docker compose -f ${DOCKER_COMPOSE_STG} down || true
                    docker compose -f ${DOCKER_COMPOSE_STG} up -d
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline concluído com sucesso. Build ${env.BUILD_NUMBER}"
        }
        failure {
            echo "Pipeline falhou. Verificar estágios anteriores."
        }
        cleanup {
            sh 'docker system prune -f || true'
        }
    }
}

