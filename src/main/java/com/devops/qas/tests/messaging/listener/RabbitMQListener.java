package com.devops.qas.tests.messaging.listener;

import com.devops.qas.tests.messaging.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQListener {

    @RabbitListener(queues = RabbitMQConfig.RECOMMENDATIONS_QUEUE)
    public void receiveMessage(Object message) {
        log.info("Mensagem recebida do RabbitMQ: {}", message);
        // Processar mensagem aqui
    }
}

