#!/usr/bin/env python3
"""
Simulador de dispositivo IoT que envia eventos de estudo via MQTT
"""

import argparse
import json
import random
import string
import time
from datetime import datetime, timedelta, timezone

import paho.mqtt.client as mqtt


DEFAULT_TOPICS = [
    "recommendations/topic",
    "telemetry/study",
    "students/engagement"
]

CATEGORIES = ["DevOps", "Cloud", "Data Science", "Backend", "Frontend"]
COURSES = [
    "Kubernetes Hands-on",
    "AWS Practitioner",
    "Python para Dados",
    "Spring Boot Expert",
    "React Avançado"
]


def random_device_id() -> str:
    prefix = random.choice(["iot", "mobile", "web"])
    suffix = "".join(random.choices(string.ascii_lowercase + string.digits, k=6))
    return f"{prefix}-{suffix}"


def generate_payload(student_id: int | None = None) -> dict:
    now = datetime.now(timezone.utc)
    duration = random.randint(15, 120)
    engagement = round(random.uniform(0.4, 0.98), 2)
    course = random.choice(COURSES)
    category = random.choice(CATEGORIES)

    return {
        "studentId": student_id or random.randint(1000, 2000),
        "deviceId": random_device_id(),
        "category": category,
        "courseName": course,
        "durationMinutes": duration,
        "engagementScore": engagement,
        "startTime": (now - timedelta(minutes=duration)).isoformat(),
        "endTime": now.isoformat(),
        "metadata": {
            "focusLevel": round(random.uniform(0.5, 1.0), 2),
            "interruptions": random.randint(0, 3),
            "notes": random.choice([
                "Estudando para certificação",
                "Revisando conteúdo",
                "Sessão de prática",
                "Mentoria guiada"
            ])
        }
    }


def main():
    parser = argparse.ArgumentParser(description="Simulador de dispositivo MQTT")
    parser.add_argument("--broker", default="localhost", help="Host do broker MQTT")
    parser.add_argument("--port", type=int, default=1883, help="Porta do broker MQTT")
    parser.add_argument("--topic", default="recommendations/topic", help="Tópico MQTT para publicação")
    parser.add_argument("--interval", type=float, default=5.0, help="Intervalo entre mensagens (segundos)")
    parser.add_argument("--student-id", type=int, help="ID fixo do estudante (opcional)")
    parser.add_argument("--random-topic", action="store_true", help="Publicar em tópicos aleatórios")
    parser.add_argument("--count", type=int, default=0, help="Quantidade de mensagens (0 = infinito)")
    args = parser.parse_args()

    client = mqtt.Client()
    client.connect(args.broker, args.port, 60)

    print("=" * 60)
    print("Simulador de dispositivo MQTT iniciado")
    print(f"Broker: {args.broker}:{args.port}")
    print(f"Tópico base: {args.topic}")
    print(f"Intervalo: {args.interval}s")
    print(f"Quantidade: {'infinito' if args.count == 0 else args.count}")
    print("=" * 60)

    sent = 0
    try:
        while True:
            payload = generate_payload(args.student_id)
            topic = random.choice(DEFAULT_TOPICS) if args.random_topic else args.topic
            message = json.dumps(payload, ensure_ascii=False)

            client.publish(topic, message, qos=1)
            sent += 1

            print(f"[{datetime.now().isoformat()}] #{sent} → {topic}")
            print(message)
            print("-" * 40)

            if 0 < args.count == sent:
                break

            time.sleep(args.interval)
    except KeyboardInterrupt:
        print("\nSimulação interrompida pelo usuário.")
    finally:
        client.disconnect()
        print("Conexão MQTT encerrada.")


if __name__ == "__main__":
    main()

