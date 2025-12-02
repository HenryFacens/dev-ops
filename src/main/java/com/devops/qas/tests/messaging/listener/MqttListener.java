package com.devops.qas.tests.messaging.listener;

import com.devops.qas.tests.telemetry.service.TelemetryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttListener {

    private final TelemetryEventService telemetryEventService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<?> message) {
        String payload = message.getPayload().toString();
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        log.info("Mensagem MQTT recebida do tópico {}: {}", topic, payload);
        telemetryEventService.processIncomingPayload(payload);
    }
}

