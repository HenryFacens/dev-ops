package com.devops.qas.tests.messaging.controller;

import com.devops.qas.tests.messaging.service.MqttService;
import com.devops.qas.tests.messaging.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessagingController {

    private final RabbitMQService rabbitMQService;
    private final MqttService mqttService;

    @PostMapping("/rabbitmq/send")
    public ResponseEntity<String> sendRabbitMQMessage(@RequestBody Map<String, Object> message) {
        rabbitMQService.sendMessage(message);
        return ResponseEntity.ok("Mensagem enviada para RabbitMQ com sucesso!");
    }

    @PostMapping("/mqtt/publish")
    public ResponseEntity<String> publishMqttMessage(
            @RequestParam(required = false) String topic,
            @RequestBody String message) {
        if (topic != null && !topic.isEmpty()) {
            mqttService.publishMessage(topic, message);
        } else {
            mqttService.publishMessage(message);
        }
        return ResponseEntity.ok("Mensagem MQTT publicada com sucesso!");
    }
}

