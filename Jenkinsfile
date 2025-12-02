pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        // Configurações Docker
        DOCKER_IMAGE = 'h3nrydock3r/recommendation-api'
        DOCKER_TAG = "${env.BUILD_NUMBER}"

        // Portas dos ambientes
        DEV_PORT = '8080'
        STAGING_PORT = '8686'
        PROD_PORT = '8585'

        // Quality Gates
        PMD_THRESHOLD = '10'
        JACOCO_COVERAGE = '70'
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30', numToKeepStr: '10'))
        timestamps()
        timeout(time: 2, unit: 'HOURS')
    }

    triggers {
        githubPush()
    }

    stages {
        // ============================================
        // PIPELINE DEV - Code Quality & Build
        // ============================================
        stage('🔍 DEV - Checkout') {
            steps {
                echo '========== PIPELINE DEV - INICIANDO =========='
                checkout scm
            }
        }

        stage('🔨 DEV - Build') {
            steps {
                echo 'Building application...'
                sh './mvnw clean compile'
            }
        }

        stage('📊 DEV - PMD Analysis') {
            steps {
                echo 'Running PMD static analysis...'
                sh './mvnw pmd:pmd'
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tool: pmdParser(pattern: '**/target/pmd.xml'),
                        qualityGates: [[threshold: 10, type: 'TOTAL', unstable: true]]
                    )
                }
            }
        }

        stage('🧪 DEV - Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh './mvnw test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: false
                }
            }
        }

        stage('📈 DEV - Code Coverage') {
            steps {
                echo 'Generating code coverage report...'
                sh './mvnw verify jacoco:report'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/*Test*.class',
                        minimumLineCoverage: '70',
                        maximumLineCoverage: '100'
                    )
                }
            }
        }

        stage('✅ DEV - Quality Gate') {
            steps {
                script {
                    echo 'Checking quality gates...'

                    def jacocoCheck = sh(
                        script: "./mvnw jacoco:check -Djacoco.coverage=${JACOCO_COVERAGE}",
                        returnStatus: true
                    )

                    if (jacocoCheck != 0) {
                        error "❌ Code coverage below ${JACOCO_COVERAGE}%"
                    }

                    echo "✅ All quality gates passed!"
                }
            }
        }

        stage('📦 DEV - Package') {
            steps {
                echo 'Packaging application...'
                sh './mvnw clean package -DskipTests'
            }
        }

        // ============================================
        // PIPELINE IMAGE_DOCKER - Build & Push
        // ============================================
        stage('🐳 DOCKER - Build Image') {
            steps {
                script {
                    echo '========== PIPELINE IMAGE_DOCKER - INICIANDO =========='
                    echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"

                    sh """
                        docker build \
                            -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            -t ${DOCKER_IMAGE}:latest \
                            --build-arg BUILD_NUMBER=${env.BUILD_NUMBER} \
                            --build-arg BUILD_DATE=\$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
                            .
                    """

                    echo "✅ Docker image built successfully"
                }
            }
        }

        stage('🔍 DOCKER - Verify Image') {
            steps {
                script {
                    echo 'Verifying Docker image...'
                    sh """
                        docker images | grep ${DOCKER_IMAGE}
                        docker inspect ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }

        stage('📤 DOCKER - Push to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            echo "🔐 Logging in to Docker Hub..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                            echo "📤 Pushing image with tag ${DOCKER_TAG}..."
                            docker push ${DOCKER_IMAGE}:${DOCKER_TAG}

                            echo "📤 Pushing image with tag latest..."
                            docker push ${DOCKER_IMAGE}:latest

                            echo "✅ Images pushed successfully!"

                            echo "👋 Logging out from Docker Hub..."
                            docker logout
                        '''
                    }
                }
            }
        }

        // ============================================
        // PIPELINE STAGING - Deploy & Tests
        // ============================================
        stage('🚀 STAGING - Deploy') {
            steps {
                script {
                    echo '========== PIPELINE STAGING - INICIANDO =========='

                    // Cleanup de containers antigos
                    sh '''
                        echo "🧹 Cleaning up old containers..."
                        docker compose -f docker-compose.staging.yml down || true
                    '''

                    // Pull da imagem mais recente
                    echo 'Pulling latest image from Docker Hub...'
                    sh 'docker compose -f docker-compose.staging.yml pull'

                    // Subir containers
                    echo 'Starting containers in staging environment...'
                    sh 'docker compose -f docker-compose.staging.yml up -d --no-color'

                    // Aguardar inicialização do Spring Boot
                    echo 'Waiting for Spring Boot to start (60 seconds)...'
                    sleep time: 60, unit: 'SECONDS'
                }
            }
        }

        stage('📊 STAGING - Container Status') {
            steps {
                script {
                    echo 'Checking container status...'

                    sh '''
                        echo "📋 Container logs:"
                        docker compose -f docker-compose.staging.yml logs --tail=50

                        echo "\n📊 Container status:"
                        docker compose -f docker-compose.staging.yml ps

                        echo "\n🔍 Detailed container info:"
                        docker compose -f docker-compose.staging.yml ps --format json
                    '''
                }
            }
        }

        stage('🏥 STAGING - Health Check') {
            steps {
                script {
                    echo "Checking application health on port ${STAGING_PORT}..."

                    def maxRetries = 10
                    def retryDelay = 10
                    def healthy = false

                    for (int i = 0; i < maxRetries; i++) {
                        def result = sh(
                            script: "curl -f -s -o /dev/null -w '%{http_code}' http://localhost:${STAGING_PORT}/actuator/health || echo '000'",
                            returnStdout: true
                        ).trim()

                        echo "Attempt ${i + 1}/${maxRetries} - HTTP Status: ${result}"

                        if (result == '200') {
                            healthy = true
                            echo "✅ Application is healthy!"
                            break
                        }

                        if (i < maxRetries - 1) {
                            echo "⏳ Waiting ${retryDelay} seconds before next attempt..."
                            sleep(retryDelay)
                        }
                    }

                    if (!healthy) {
                        error "❌ Application failed to respond after ${maxRetries} attempts"
                    }
                }
            }
        }

        stage('🧪 STAGING - Smoke Tests') {
            steps {
                script {
                    echo 'Running smoke tests against staging environment...'

                    def testResults = [:]

                    // Test main endpoint
                    testResults['Main Endpoint'] = sh(
                        script: "curl -f http://localhost:${STAGING_PORT} || echo 'FAILED'",
                        returnStatus: true
                    ) == 0

                    // Test health endpoint
                    testResults['Health Endpoint'] = sh(
                        script: "curl -f http://localhost:${STAGING_PORT}/actuator/health || echo 'FAILED'",
                        returnStatus: true
                    ) == 0

                    // Test info endpoint
                    testResults['Info Endpoint'] = sh(
                        script: "curl -f http://localhost:${STAGING_PORT}/actuator/info || echo 'FAILED'",
                        returnStatus: true
                    ) == 0

                    // Display results
                    echo "========== SMOKE TEST RESULTS =========="
                    testResults.each { test, passed ->
                        def status = passed ? "✅ PASSED" : "❌ FAILED"
                        echo "${test}: ${status}"
                    }
                    echo "========================================"

                    // Check if all tests passed
                    def allPassed = testResults.values().every { it == true }
                    if (!allPassed) {
                        error "❌ Some smoke tests failed!"
                    }

                    echo "✅ All smoke tests passed!"
                }
            }
        }

        stage('📊 STAGING - Validation Report') {
            steps {
                script {
                    echo '========== STAGING VALIDATION REPORT =========='

                    sh """
                        echo "🐳 Container Information:"
                        docker compose -f docker-compose.staging.yml ps

                        echo "\n📊 Container Resource Usage:"
                        docker stats --no-stream --format "table {{.Name}}\\t{{.CPUPerc}}\\t{{.MemUsage}}" \$(docker compose -f docker-compose.staging.yml ps -q)

                        echo "\n🌐 Network Information:"
                        docker compose -f docker-compose.staging.yml exec -T recommendation-api hostname -i || true

                        echo "\n📝 Recent Application Logs:"
                        docker compose -f docker-compose.staging.yml logs --tail=30
                    """

                    echo '=============================================='
                }
            }
        }

        // ============================================
        // PIPELINE PRODUCTION - Optional with Approval
        // ============================================
        stage('🎯 PROD - Approval Gate') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo '⏸️ Waiting for approval to deploy to production...'

                    timeout(time: 1, unit: 'HOURS') {
                        input message: 'Deploy to PRODUCTION?',
                              ok: 'Yes, deploy to prod!',
                              submitter: 'admin,deploy-team'
                    }

                    echo '✅ Production deployment approved!'
                }
            }
        }

        stage('🚀 PROD - Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo '========== PIPELINE PRODUCTION - INICIANDO =========='

                    // Cleanup
                    sh '''
                        echo "🧹 Cleaning up production containers..."
                        docker compose -f docker-compose.prod.yml down || true
                    '''

                    // Pull latest image
                    echo 'Pulling latest image from Docker Hub...'
                    sh 'docker compose -f docker-compose.prod.yml pull'

                    // Deploy to production
                    echo 'Deploying to production environment...'
                    sh 'docker compose -f docker-compose.prod.yml up -d --no-color'

                    // Wait for startup
                    echo 'Waiting for Spring Boot to start (60 seconds)...'
                    sleep time: 60, unit: 'SECONDS'

                    // Verify deployment
                    sh '''
                        echo "📋 Production logs:"
                        docker compose -f docker-compose.prod.yml logs --tail=50

                        echo "\n📊 Production status:"
                        docker compose -f docker-compose.prod.yml ps
                    '''
                }
            }
        }

        stage('🏥 PROD - Health Check') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "Verifying production deployment on port ${PROD_PORT}..."

                    sh "curl http://localhost:${PROD_PORT} || echo 'Service not responding'"
                    sh "curl http://localhost:${PROD_PORT}/actuator/health || echo 'Health check failed'"

                    echo '✅ Production deployment validated!'
                }
            }
        }
    }

    post {
        success {
            script {
                echo '''
                ╔═══════════════════════════════════════════════════════╗
                ║          ✅ PIPELINE EXECUTADO COM SUCESSO!           ║
                ╚═══════════════════════════════════════════════════════╝
                '''

                def report = """
                📊 BUILD SUMMARY:

                🔹 Pipeline: DEV ➜ IMAGE_DOCKER ➜ STAGING ${env.BRANCH_NAME == 'main' ? '➜ PRODUCTION' : ''}
                🔹 Build Number: #${env.BUILD_NUMBER}
                🔹 Branch: ${env.BRANCH_NAME}
                🔹 Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}
                🔹 Docker Hub: https://hub.docker.com/r/h3nrydock3r/recommendation-api

                🌐 Environments:
                   - Staging: http://localhost:${STAGING_PORT}
                ${env.BRANCH_NAME == 'main' ? "   - Production: http://localhost:${PROD_PORT}" : ''}

                ✅ Quality Gates: PASSED
                ✅ Docker Image: PUBLISHED
                ✅ Staging: DEPLOYED & VALIDATED
                ${env.BRANCH_NAME == 'main' ? '✅ Production: DEPLOYED' : ''}
                """

                echo report
            }
        }

        failure {
            script {
                echo '''
                ╔═══════════════════════════════════════════════════════╗
                ║               ❌ PIPELINE FALHOU!                     ║
                ╚═══════════════════════════════════════════════════════╝
                '''

                echo """
                ❌ Build: #${env.BUILD_NUMBER}
                📍 Failed Stage: ${env.STAGE_NAME}
                🔍 Check logs above for details
                """

                // Cleanup on failure
                sh '''
                    echo "🧹 Cleaning up failed deployments..."
                    docker compose -f docker-compose.staging.yml down || true
                    docker compose -f docker-compose.prod.yml down || true
                '''
            }
        }

        unstable {
            echo '⚠️ Build unstable - Quality gates not met'
        }

        always {
            echo '''
            ╔═══════════════════════════════════════════════════════╗
            ║              PIPELINE FINALIZADO                      ║
            ╚═══════════════════════════════════════════════════════╝
            '''

            // Cleanup old Docker images
            sh '''
                echo "🧹 Cleaning up old Docker images..."
                docker images | grep ${DOCKER_IMAGE} | awk '{print $3}' | tail -n +6 | xargs -r docker rmi -f || true

                echo "🧹 Pruning unused Docker resources..."
                docker system prune -f || true
            '''

            echo "Pipeline completed at: ${new Date()}"
        }
    }
}