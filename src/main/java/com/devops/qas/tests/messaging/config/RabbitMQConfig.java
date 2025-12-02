package com.devops.qas.tests.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RECOMMENDATIONS_QUEUE = "recommendations.queue";
    public static final String RECOMMENDATIONS_EXCHANGE = "recommendations.exchange";
    public static final String RECOMMENDATIONS_ROUTING_KEY = "recommendations.routing.key";
    
    public static final String EMAIL_REPORTS_QUEUE = "email.reports.queue";
    public static final String EMAIL_REPORTS_EXCHANGE = "email.reports.exchange";
    public static final String EMAIL_REPORTS_ROUTING_KEY = "email.reports.routing.key";

    @Bean
    public Queue recommendationsQueue() {
        return QueueBuilder.durable(RECOMMENDATIONS_QUEUE).build();
    }

    @Bean
    public TopicExchange recommendationsExchange() {
        return new TopicExchange(RECOMMENDATIONS_EXCHANGE);
    }

    @Bean
    public Binding recommendationsBinding() {
        return BindingBuilder
                .bind(recommendationsQueue())
                .to(recommendationsExchange())
                .with(RECOMMENDATIONS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public Queue emailReportsQueue() {
        return QueueBuilder.durable(EMAIL_REPORTS_QUEUE).build();
    }

    @Bean
    public TopicExchange emailReportsExchange() {
        return new TopicExchange(EMAIL_REPORTS_EXCHANGE);
    }

    @Bean
    public Binding emailReportsBinding() {
        return BindingBuilder
                .bind(emailReportsQueue())
                .to(emailReportsExchange())
                .with(EMAIL_REPORTS_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}

