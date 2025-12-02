package com.devops.qas.tests.messaging.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttService {

    private final MessageChannel mqttOutputChannel;

    @Value("${mqtt.topic.recommendations}")
    private String defaultTopic;

    public void publishMessage(String topic, String message) {
        log.info("Publicando mensagem MQTT no tópico {}: {}", topic, message);
        mqttOutputChannel.send(MessageBuilder
                .withPayload(message)
                .setHeader("mqtt_topic", topic)
                .build());
        log.info("Mensagem MQTT publicada com sucesso!");
    }

    public void publishMessage(String message) {
        publishMessage(defaultTopic, message);
    }
}

