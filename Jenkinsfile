pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    environment {
        // Configurações Docker
        DOCKER_IMAGE = 'andprof/ac2_ca'
        DOCKER_TAG = "${env.BUILD_NUMBER}"

        // Portas dos ambientes
        STAGING_PORT = '8686'
        PROD_PORT = '8585'

        // Quality Gates
        JACOCO_COVERAGE = '99'
        PMD_THRESHOLD = '10'
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
                echo '=========================================='
                echo '       PIPELINE DEV - INICIANDO'
                echo '=========================================='
                checkout scm
            }
        }

        stage('🔨 DEV - Build') {
            steps {
                echo 'Compilando aplicação...'
                sh './mvnw clean compile'
            }
        }

        stage('📊 DEV - PMD Analysis') {
            steps {
                echo 'Executando análise estática com PMD...'
                sh './mvnw pmd:pmd'
            }
            post {
                always {
                    script {
                        // Verifica se o arquivo PMD existe
                        if (fileExists('target/pmd.xml')) {
                            recordIssues(
                                enabledForFailure: true,
                                tool: pmdParser(pattern: '**/target/pmd.xml'),
                                qualityGates: [[threshold: 10, type: 'TOTAL', unstable: true]]
                            )
                            echo "✅ Análise PMD concluída"
                        } else {
                            echo "⚠️ Arquivo PMD não encontrado, pulando análise"
                        }
                    }
                }
            }
        }

        stage('🧪 DEV - Unit Tests') {
            steps {
                echo 'Executando testes unitários...'
                sh './mvnw test'
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

        stage('📈 DEV - Code Coverage') {
            steps {
                echo 'Gerando relatório de cobertura JaCoCo...'
                sh './mvnw verify'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/*Test*.class,**/config/**,**/entity/**',
                        minimumLineCoverage: '99',
                        maximumLineCoverage: '100'
                    )
                    echo "✅ Relatório de cobertura gerado"
                }
            }
        }

        stage('✅ DEV - Quality Gate') {
            steps {
                script {
                    echo 'Verificando Quality Gates...'

                    // Verifica cobertura JaCoCo (99%)
                    def jacocoCheck = sh(
                        script: './mvnw jacoco:check',
                        returnStatus: true
                    )

                    if (jacocoCheck != 0) {
                        error "❌ Cobertura de código abaixo de ${JACOCO_COVERAGE}%"
                    }

                    echo "✅ Quality Gates aprovados! Cobertura >= ${JACOCO_COVERAGE}%"
                }
            }
        }

        stage('📦 DEV - Package') {
            steps {
                echo 'Gerando artefato JAR...'
                sh './mvnw clean package -DskipTests'

                script {
                    // Verifica se o JAR foi criado
                    def jarFile = sh(
                        script: 'ls target/*.jar | grep -v original',
                        returnStdout: true
                    ).trim()
                    echo "✅ Artefato gerado: ${jarFile}"
                }
            }
        }

        // ============================================
        // PIPELINE IMAGE_DOCKER - Build & Push
        // ============================================
        stage('🐳 DOCKER - Build Image') {
            steps {
                script {
                    echo '=========================================='
                    echo '    PIPELINE IMAGE_DOCKER - INICIANDO'
                    echo '=========================================='
                    echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"

                    sh """
                        docker build \
                            -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            -t ${DOCKER_IMAGE}:latest \
                            --build-arg JAR_FILE=target/*.jar \
                            --build-arg BUILD_NUMBER=${env.BUILD_NUMBER} \
                            --build-arg BUILD_DATE=\$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
                            .
                    """

                    echo "✅ Imagem Docker construída com sucesso"
                }
            }
        }

        stage('🔍 DOCKER - Verify Image') {
            steps {
                script {
                    echo 'Verificando imagem Docker...'
                    sh """
                        echo "📋 Listando imagens:"
                        docker images | grep ${DOCKER_IMAGE}

                        echo "\n🔍 Inspecionando imagem:"
                        docker inspect ${DOCKER_IMAGE}:${DOCKER_TAG} --format='{{.Created}} | {{.Size}} bytes'
                    """
                    echo "✅ Imagem verificada"
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
                            echo "🔐 Autenticando no Docker Hub..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                            echo "📤 Enviando imagem com tag ${DOCKER_TAG}..."
                            docker push ${DOCKER_IMAGE}:${DOCKER_TAG}

                            echo "📤 Enviando imagem com tag latest..."
                            docker push ${DOCKER_IMAGE}:latest

                            echo "✅ Imagens enviadas com sucesso!"

                            echo "👋 Logout do Docker Hub..."
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
                    echo '=========================================='
                    echo '      PIPELINE STAGING - INICIANDO'
                    echo '=========================================='

                    // Cleanup de containers antigos
                    sh '''
                        echo "🧹 Limpando containers antigos..."
                        docker compose -f docker-compose.staging.yml down -v || true
                    '''

                    // Pull da imagem mais recente do Docker Hub
                    echo 'Baixando última imagem do Docker Hub...'
                    sh 'docker compose -f docker-compose.staging.yml pull'

                    // Subir containers (PostgreSQL + API)
                    echo 'Iniciando containers em staging (PostgreSQL + API)...'
                    sh 'docker compose -f docker-compose.staging.yml up -d --no-color'

                    // Aguardar inicialização
                    echo 'Aguardando inicialização do Spring Boot e PostgreSQL (60 segundos)...'
                    sleep time: 60, unit: 'SECONDS'
                }
            }
        }

        stage('📊 STAGING - Container Status') {
            steps {
                script {
                    echo 'Verificando status dos containers...'

                    sh '''
                        echo "📋 Logs dos containers:"
                        docker compose -f docker-compose.staging.yml logs --tail=50

                        echo "\n📊 Status dos containers:"
                        docker compose -f docker-compose.staging.yml ps

                        echo "\n🔍 Detalhes dos containers:"
                        docker compose -f docker-compose.staging.yml ps --format json | jq '.'
                    '''
                }
            }
        }

        stage('🏥 STAGING - Health Check') {
            steps {
                script {
                    echo "Verificando saúde da aplicação na porta ${STAGING_PORT}..."

                    def maxRetries = 15
                    def retryDelay = 10
                    def healthy = false

                    for (int i = 0; i < maxRetries; i++) {
                        def result = sh(
                            script: """
                                curl -f -s -o /dev/null -w '%{http_code}' \
                                http://localhost:${STAGING_PORT}/actuator/health 2>/dev/null || echo '000'
                            """,
                            returnStdout: true
                        ).trim()

                        echo "Tentativa ${i + 1}/${maxRetries} - HTTP Status: ${result}"

                        if (result == '200') {
                            healthy = true

                            // Buscar detalhes do health
                            def healthDetails = sh(
                                script: "curl -s http://localhost:${STAGING_PORT}/actuator/health",
                                returnStdout: true
                            ).trim()

                            echo "✅ Aplicação está saudável!"
                            echo "Detalhes: ${healthDetails}"
                            break
                        }

                        if (i < maxRetries - 1) {
                            echo "⏳ Aguardando ${retryDelay} segundos..."
                            sleep(retryDelay)
                        }
                    }

                    if (!healthy) {
                        // Mostrar logs antes de falhar
                        sh 'docker compose -f docker-compose.staging.yml logs --tail=100'
                        error "❌ Aplicação não respondeu após ${maxRetries} tentativas"
                    }
                }
            }
        }

        stage('🧪 STAGING - Smoke Tests') {
            steps {
                script {
                    echo 'Executando smoke tests em staging...'

                    def testResults = [:]

                    // Test 1: Health endpoint
                    echo "Testing Health endpoint..."
                    testResults['Health'] = sh(
                        script: "curl -f -s http://localhost:${STAGING_PORT}/actuator/health",
                        returnStatus: true
                    ) == 0

                    // Test 2: Info endpoint
                    echo "Testing Info endpoint..."
                    testResults['Info'] = sh(
                        script: "curl -f -s http://localhost:${STAGING_PORT}/actuator/info",
                        returnStatus: true
                    ) == 0

                    // Test 3: Swagger UI
                    echo "Testing Swagger UI..."
                    testResults['Swagger'] = sh(
                        script: "curl -f -s -o /dev/null http://localhost:${STAGING_PORT}/swagger-ui/index.html",
                        returnStatus: true
                    ) == 0

                    // Test 4: API Docs
                    echo "Testing API Docs..."
                    testResults['API Docs'] = sh(
                        script: "curl -f -s http://localhost:${STAGING_PORT}/v3/api-docs",
                        returnStatus: true
                    ) == 0

                    // Test 5: Database Connection
                    echo "Testing Database Connection..."
                    def dbCheck = sh(
                        script: '''
                            docker compose -f docker-compose.staging.yml exec -T database \
                            psql -U postgres -d sapi -c "SELECT 1" > /dev/null 2>&1
                        ''',
                        returnStatus: true
                    )
                    testResults['Database'] = (dbCheck == 0)

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
                        error "❌ Alguns smoke tests falharam!"
                    }

                    echo "✅ Todos os smoke tests passaram!"
                }
            }
        }

        stage('📊 STAGING - Validation Report') {
            steps {
                script {
                    echo '========== RELATÓRIO DE VALIDAÇÃO - STAGING =========='

                    sh """
                        echo "🐳 Containers em Execução:"
                        docker compose -f docker-compose.staging.yml ps

                        echo "\n📊 Uso de Recursos:"
                        docker stats --no-stream --format "table {{.Name}}\\t{{.CPUPerc}}\\t{{.MemUsage}}\\t{{.NetIO}}" \
                        \$(docker compose -f docker-compose.staging.yml ps -q)

                        echo "\n🌐 Endpoints Disponíveis:"
                        echo "  - API: http://localhost:${STAGING_PORT}"
                        echo "  - Health: http://localhost:${STAGING_PORT}/actuator/health"
                        echo "  - Swagger: http://localhost:${STAGING_PORT}/swagger-ui/index.html"
                        echo "  - API Docs: http://localhost:${STAGING_PORT}/v3/api-docs"

                        echo "\n💾 Volumes:"
                        docker volume ls | grep staging || echo "Nenhum volume específico"

                        echo "\n📝 Últimos Logs da API:"
                        docker compose -f docker-compose.staging.yml logs --tail=30 api

                        echo "\n📝 Últimos Logs do Database:"
                        docker compose -f docker-compose.staging.yml logs --tail=20 database
                    """

                    echo '======================================================'
                }
            }
        }

        // ============================================
        // PIPELINE PRODUCTION - Deploy com Aprovação
        // ============================================
        stage('🎯 PROD - Approval Gate') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo '=========================================='
                    echo '   AGUARDANDO APROVAÇÃO PARA PRODUÇÃO'
                    echo '=========================================='

                    timeout(time: 1, unit: 'HOURS') {
                        input message: '🚀 Aprovar deploy em PRODUÇÃO?',
                              ok: 'Sim, fazer deploy!',
                              submitter: 'admin,deploy-team',
                              parameters: [
                                  choice(
                                      name: 'CONFIRM',
                                      choices: ['SIM', 'NAO'],
                                      description: 'Confirme o deploy em produção'
                                  )
                              ]
                    }

                    echo '✅ Deploy em produção APROVADO!'
                }
            }
        }

        stage('🚀 PROD - Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo '=========================================='
                    echo '      PIPELINE PRODUCTION - INICIANDO'
                    echo '=========================================='

                    // Cleanup
                    sh '''
                        echo "🧹 Limpando ambiente de produção..."
                        docker compose -f docker-compose.prod.yml down -v || true
                    '''

                    // Pull latest image
                    echo 'Baixando última imagem do Docker Hub...'
                    sh 'docker compose -f docker-compose.prod.yml pull'

                    // Deploy to production
                    echo 'Iniciando containers em PRODUÇÃO (PostgreSQL + API)...'
                    sh 'docker compose -f docker-compose.prod.yml up -d --no-color'

                    // Wait for startup
                    echo 'Aguardando inicialização (60 segundos)...'
                    sleep time: 60, unit: 'SECONDS'

                    // Verify deployment
                    sh '''
                        echo "📋 Logs de produção:"
                        docker compose -f docker-compose.prod.yml logs --tail=50

                        echo "\n📊 Status de produção:"
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
                    echo "Verificando deploy de produção na porta ${PROD_PORT}..."

                    def maxRetries = 15
                    def healthy = false

                    for (int i = 0; i < maxRetries; i++) {
                        def result = sh(
                            script: "curl -f -s -o /dev/null -w '%{http_code}' http://localhost:${PROD_PORT}/actuator/health || echo '000'",
                            returnStdout: true
                        ).trim()

                        echo "Tentativa ${i + 1}/${maxRetries} - Status: ${result}"

                        if (result == '200') {
                            healthy = true
                            echo "✅ Produção está saudável!"
                            break
                        }

                        if (i < maxRetries - 1) {
                            sleep(10)
                        }
                    }

                    if (!healthy) {
                        sh 'docker compose -f docker-compose.prod.yml logs --tail=100'
                        error "❌ Produção não respondeu"
                    }
                }
            }
        }

        stage('🧪 PROD - Final Validation') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo 'Validação final de produção...'

                    sh """
                        echo "Testing production endpoints..."
                        curl -f http://localhost:${PROD_PORT}/actuator/health
                        curl -f http://localhost:${PROD_PORT}/actuator/info

                        echo "\n✅ Produção validada e operacional!"
                    """
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
                📊 RESUMO DO BUILD:

                🔹 Pipeline: DEV ➜ IMAGE_DOCKER ➜ STAGING ${env.BRANCH_NAME == 'main' ? '➜ PRODUCTION' : ''}
                🔹 Build: #${env.BUILD_NUMBER}
                🔹 Branch: ${env.BRANCH_NAME}
                🔹 Commit: ${env.GIT_COMMIT?.take(8)}

                🐳 Docker:
                   - Imagem: ${DOCKER_IMAGE}:${DOCKER_TAG}
                   - Latest: ${DOCKER_IMAGE}:latest
                   - Hub: https://hub.docker.com/r/andprof/ac2_ca

                🌐 Ambientes:
                   - Staging: http://localhost:${STAGING_PORT}
                   - Swagger (Staging): http://localhost:${STAGING_PORT}/swagger-ui/index.html
                ${env.BRANCH_NAME == 'main' ? "   - Production: http://localhost:${PROD_PORT}" : ''}
                ${env.BRANCH_NAME == 'main' ? "   - Swagger (Prod): http://localhost:${PROD_PORT}/swagger-ui/index.html" : ''}

                ✅ Quality Gates: PASSED (Coverage >= ${JACOCO_COVERAGE}%)
                ✅ PMD Analysis: PASSED
                ✅ Unit Tests: PASSED
                ✅ Docker Image: PUBLISHED
                ✅ Staging: DEPLOYED & VALIDATED
                ${env.BRANCH_NAME == 'main' ? '✅ Production: DEPLOYED & VALIDATED' : ''}

                💾 Database:
                   - Staging: PostgreSQL (sapi)
                ${env.BRANCH_NAME == 'main' ? '   - Production: PostgreSQL (papi)' : ''}
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
                📍 Stage que falhou: ${env.STAGE_NAME}
                🔍 Verifique os logs acima para mais detalhes

                💡 Dicas de troubleshooting:
                   - Verifique os logs do container
                   - Confirme se as portas não estão em uso
                   - Verifique se o PostgreSQL iniciou corretamente
                """

                // Cleanup on failure
                sh '''
                    echo "🧹 Limpando ambientes após falha..."
                    docker compose -f docker-compose.staging.yml down -v || true
                    docker compose -f docker-compose.prod.yml down -v || true
                '''
            }
        }

        unstable {
            echo '⚠️ Build instável - Quality Gates não atingiram os requisitos'
        }

        always {
            echo '''
            ╔═══════════════════════════════════════════════════════╗
            ║              PIPELINE FINALIZADO                      ║
            ╚═══════════════════════════════════════════════════════╝
            '''

            // Cleanup old Docker images (keep last 5)
            sh '''
                echo "🧹 Limpando imagens Docker antigas..."
                docker images | grep ${DOCKER_IMAGE} | awk '{print $3}' | tail -n +6 | xargs -r docker rmi -f || true

                echo "🧹 Limpando recursos Docker não utilizados..."
                docker system prune -f --volumes || true
            '''

            echo "⏰ Pipeline finalizado em: ${new Date()}"
        }
    }
}