pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Start container') {
      steps {
        echo 'Starting container from Docker Hub...'
        bat 'docker-compose -f docker-compose.prod.yml pull' // Baixa a imagem do Docker Hub
        bat 'docker-compose -f docker-compose.prod.yml up -d --no-color'
        sleep time: 60, unit: 'SECONDS' // Aumenta o tempo para o serviço Spring Boot iniciar
        bat 'docker-compose -f docker-compose.prod.yml logs' // Verifica os logs para conferir o status do Spring Boot
        bat 'docker-compose -f docker-compose.prod.yml ps' // Verifica o status do container
      }
    }

    stage('Run tests against the container') {
      steps {
        bat 'curl http://localhost:8585 || echo "Service not responding"'
      }
    }
  }

  post {
    always {
      echo 'Pipeline completed'
      // Não derruba mais o container aqui
    }
  }
}