# Guia de Integração: MQTT, RabbitMQ e LangChain4j

Este documento descreve como usar as integrações de MQTT, RabbitMQ e LangChain4j adicionadas ao projeto.

## 📋 Índice

1. [RabbitMQ](#rabbitmq)
2. [MQTT](#mqtt)
3. [Telemetria MQTT](#telemetria-mqtt)
4. [LangChain4j (AI)](#langchain4j-ai)
5. [Scripts Úteis](#scripts-úteis)

---

## RabbitMQ

### Configuração

As configurações do RabbitMQ estão no `application.properties`:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.template.exchange=recommendations.exchange
spring.rabbitmq.template.routing-key=recommendations.routing.key
```

### Uso

#### Enviar Mensagem

```java
@Autowired
private RabbitMQService rabbitMQService;

// Enviar mensagem com routing key padrão
rabbitMQService.sendMessage(objeto);

// Enviar mensagem com routing key customizado
rabbitMQService.sendMessage("custom.routing.key", objeto);
```

#### Receber Mensagens

O `RabbitMQListener` está configurado para receber mensagens automaticamente da fila `recommendations.queue`. Para processar mensagens, edite o método `receiveMessage` em `RabbitMQListener.java`.

#### API REST

```bash
# Enviar mensagem via API
POST /api/messaging/rabbitmq/send
Content-Type: application/json

{
  "message": "Conteúdo da mensagem",
  "studentId": 123
}
```

### Interface de Gerenciamento

Acesse o RabbitMQ Management em: http://localhost:15672
- Usuário: `guest`
- Senha: `guest`

---

## MQTT

### Configuração

As configurações do MQTT estão no `application.properties`:

```properties
mqtt.broker.url=tcp://localhost:1883
mqtt.client.id=spring-boot-client
mqtt.topic.recommendations=recommendations/topic
mqtt.qos=1
```

### Uso

#### Publicar Mensagem

```java
@Autowired
private MqttService mqttService;

// Publicar no tópico padrão
mqttService.publishMessage("Mensagem MQTT");

// Publicar em tópico customizado
mqttService.publishMessage("custom/topic", "Mensagem");
```

#### Receber Mensagens

O `MqttListener` está configurado para receber mensagens automaticamente do tópico `recommendations/topic`. Os payloads são encaminhados ao `TelemetryEventService`, que persiste os dados na tabela `study_session_events`.

#### API REST

```bash
# Publicar mensagem no tópico padrão
POST /api/messaging/mqtt/publish
Content-Type: text/plain

Mensagem MQTT aqui

# Publicar em tópico customizado
POST /api/messaging/mqtt/publish?topic=custom/topic
Content-Type: text/plain

Mensagem MQTT aqui
```

---

## Telemetria MQTT

### Entidade Persistida

Cada mensagem recebida é convertida em `StudySessionEvent`, armazenada com os campos:

| Campo             | Descrição                                  |
|-------------------|--------------------------------------------|
| `studentId`       | Estudante associado                        |
| `deviceId`        | Identificação do dispositivo               |
| `category`        | Categoria do curso                         |
| `courseName`      | Nome do curso                              |
| `durationMinutes` | Duração da sessão                          |
| `engagementScore` | Índice de engajamento (0-1)                |
| `startTime`       | Início da sessão                           |
| `endTime`         | Fim da sessão                              |
| `metadata`        | Dados extras enviados pelo dispositivo     |

### Endpoints REST

```bash
# Buscar eventos (com filtros opcionais)
GET /api/telemetry/events?studentId=123&category=DevOps

# Ingestão manual (útil para testes)
POST /api/telemetry/events
Content-Type: application/json
{
  "studentId": 123,
  "deviceId": "iot-abc123",
  "category": "DevOps",
  "courseName": "Kubernetes Hands-on",
  "durationMinutes": 45,
  "engagementScore": 0.82,
  "startTime": "2024-02-01T10:00:00Z",
  "endTime": "2024-02-01T10:45:00Z",
  "metadata": {
    "focusLevel": 0.9,
    "interruptions": 1
  }
}
```

### Simulador MQTT

Use o script `mqtt_device_simulator.py` para gerar eventos realistas:

```bash
pip install -r requirements_mqtt_simulator.txt
python mqtt_device_simulator.py --interval 2 --random-topic
```

Mais detalhes em `docs/MQTT_SIMULATOR.md`.

---

## LangChain4j (AI)

### Configuração

As configurações do LangChain4j estão no `application.properties`:

```properties
langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY:your-api-key-here}
langchain4j.open-ai.chat-model.model-name=gpt-3.5-turbo
langchain4j.open-ai.chat-model.temperature=0.7
langchain4j.open-ai.chat-model.timeout=60s
```

**⚠️ IMPORTANTE:** Configure a variável de ambiente `OPENAI_API_KEY` com sua chave da OpenAI antes de usar.

### Uso

#### Gerar Recomendação com IA

```java
@Autowired
private AIService aiService;

String studentProfile = "Estudante interessado em DevOps e Cloud Computing";
String recommendation = aiService.generateRecommendation(studentProfile);
```

#### Chat com IA

```java
String userMessage = "Qual é a melhor forma de aprender Docker?";
String response = aiService.chat(userMessage);
```

#### Analisar Feedback

```java
String feedback = "A recomendação foi muito útil!";
String analysis = aiService.analyzeRecommendationFeedback(feedback);
```

#### API REST

```bash
# Gerar recomendação
POST /api/ai/recommendation
Content-Type: text/plain

Estudante interessado em DevOps e Cloud Computing

# Chat com IA
POST /api/ai/chat
Content-Type: text/plain

Qual é a melhor forma de aprender Docker?

# Analisar feedback
POST /api/ai/analyze-feedback
Content-Type: text/plain

A recomendação foi muito útil!
```

---

## 🐳 Docker Compose

O projeto inclui um `docker-compose.yml` configurado com:

- **RabbitMQ**: Porta 5672 (AMQP) e 15672 (Management UI)
- **Mosquitto MQTT**: Porta 1883 (MQTT)

Para iniciar os serviços:

```bash
docker-compose up -d
```

Para parar:

```bash
docker-compose down
```

---

## 📝 Exemplos de Integração

### Exemplo: Enviar Recomendação via RabbitMQ e Processar com IA

```java
@Service
@RequiredArgsConstructor
public class RecommendationIntegrationService {
    
    private final RabbitMQService rabbitMQService;
    private final AIService aiService;
    
    public void processRecommendation(Long studentId) {
        // Gerar recomendação com IA
        String profile = "Estudante ID: " + studentId;
        String aiRecommendation = aiService.generateRecommendation(profile);
        
        // Enviar para RabbitMQ
        Map<String, Object> message = Map.of(
            "studentId", studentId,
            "recommendation", aiRecommendation,
            "timestamp", System.currentTimeMillis()
        );
        rabbitMQService.sendMessage(message);
    }
}
```

### Exemplo: Publicar Evento MQTT após Processamento

```java
@Service
@RequiredArgsConstructor
public class EventService {
    
    private final MqttService mqttService;
    
    public void publishRecommendationEvent(Long studentId, String recommendation) {
        String event = String.format(
            "{\"studentId\":%d,\"recommendation\":\"%s\",\"timestamp\":%d}",
            studentId, recommendation, System.currentTimeMillis()
        );
        mqttService.publishMessage("events/recommendations", event);
    }
}
```

---

## Scripts Úteis

| Script                     | Descrição                                                 | Guia                              |
|----------------------------|-----------------------------------------------------------|-----------------------------------|
| `email_report_consumer.py` | Consome relatórios RabbitMQ e envia emails via Gmail      | `docs/EMAIL_REPORTS_SETUP.md`     |
| `mqtt_device_simulator.py` | Simula dispositivos IoT publicando telemetria via MQTT    | `docs/MQTT_SIMULATOR.md`          |

---

## 🔧 Troubleshooting

### RabbitMQ não conecta
- Verifique se o RabbitMQ está rodando: `docker-compose ps`
- Verifique as credenciais no `application.properties`
- Acesse o Management UI para verificar conexões

### MQTT não publica/recebe mensagens
- Verifique se o Mosquitto está rodando: `docker-compose ps`
- Verifique a URL do broker no `application.properties`
- Teste com um cliente MQTT externo (ex: MQTT.fx)

### LangChain4j retorna erro
- Verifique se a `OPENAI_API_KEY` está configurada
- Verifique se há créditos na conta OpenAI
- Verifique os logs da aplicação para detalhes do erro

---

## 📚 Recursos Adicionais

- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [MQTT Specification](https://mqtt.org/mqtt-specification/)
- [LangChain4j Documentation](https://github.com/langchain4j/langchain4j)

