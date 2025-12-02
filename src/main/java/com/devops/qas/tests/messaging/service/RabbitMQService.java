package com.devops.qas.tests.messaging.service;

import com.devops.qas.tests.messaging.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Object message) {
        log.info("Enviando mensagem para RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECOMMENDATIONS_EXCHANGE,
                RabbitMQConfig.RECOMMENDATIONS_ROUTING_KEY,
                message
        );
        log.info("Mensagem enviada com sucesso!");
    }

    public void sendMessage(String routingKey, Object message) {
        log.info("Enviando mensagem para RabbitMQ com routing key {}: {}", routingKey, message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECOMMENDATIONS_EXCHANGE,
                routingKey,
                message
        );
        log.info("Mensagem enviada com sucesso!");
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        log.info("Enviando mensagem para RabbitMQ - Exchange: {}, Routing Key: {}, Message: {}", exchange, routingKey, message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("Mensagem enviada com sucesso!");
    }
}

