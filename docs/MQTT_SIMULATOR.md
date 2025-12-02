# Simulador de Dispositivo MQTT

Este guia mostra como executar o script `mqtt_device_simulator.py` que emula um dispositivo enviando eventos de estudo via MQTT.

## ⚙️ Requisitos

- Python 3.8+
- Broker MQTT (use o Mosquitto do `docker-compose.yml`)
- Dependências Python:

```bash
pip install -r requirements_mqtt_simulator.txt
```

## 🚀 Executando o simulador

```bash
python mqtt_device_simulator.py --broker localhost --port 1883 --topic recommendations/topic --interval 3
```

### Executando via Docker Compose

O `docker-compose.yml` já inclui o serviço `mqtt-simulator`. Para subir o broker e o simulador juntos:

```bash
docker compose up -d mosquitto mqtt-simulator
```

Esse serviço roda continuamente, publicando eventos a cada 5 segundos usando o broker `mosquitto` e tópicos aleatórios pré-configurados.

### Principais argumentos

| Argumento        | Descrição                                          | Padrão                |
|------------------|----------------------------------------------------|----------------------|
| `--broker`       | Host do broker MQTT                                | `localhost`          |
| `--port`         | Porta do broker                                    | `1883`               |
| `--topic`        | Tópico para publicação                             | `recommendations/topic` |
| `--interval`     | Intervalo entre mensagens (segundos)               | `5`                  |
| `--count`        | Quantidade de mensagens (0 = infinito)             | `0`                  |
| `--student-id`   | ID fixo do estudante (opcional)                    | aleatório            |
| `--random-topic` | Publica em tópicos aleatórios pré-definidos        | `False`              |

## 🧪 Exemplo completo

1. Inicie o Mosquitto:
   ```bash
   docker-compose up -d mosquitto
   ```

2. Execute o simulador:
   ```bash
   python mqtt_device_simulator.py --interval 2 --random-topic
   ```

3. Veja os eventos sendo persistidos:
   ```bash
   curl http://localhost:8080/api/telemetry/events
   ```

## 🔍 Debug rápido

- Para inspecionar as mensagens:
  ```bash
  mosquitto_sub -h localhost -t "recommendations/#" -v
  ```

- Logs do aplicativo Spring:
  ```bash
  mvn spring-boot:run
  ```

## 📚 Payload gerado

```json
{
  "studentId": 1234,
  "deviceId": "iot-ab12cd",
  "category": "DevOps",
  "courseName": "Kubernetes Hands-on",
  "durationMinutes": 45,
  "engagementScore": 0.87,
  "startTime": "2024-02-01T10:00:00Z",
  "endTime": "2024-02-01T10:45:00Z",
  "metadata": {
    "focusLevel": 0.92,
    "interruptions": 1,
    "notes": "Sessão de prática"
  }
}
```

Esses dados são recebidos pela API, gravados no banco (tabela `study_session_events`) e podem ser consultados pelos endpoints em `/api/telemetry`.

