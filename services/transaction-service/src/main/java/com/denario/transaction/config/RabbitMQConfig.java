package com.denario.transaction.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${denario.rabbitmq.exchanges.transaction}")
    private String transactionExchange;

    @Value("${denario.rabbitmq.queues.transaction-created}")
    private String transactionCreatedQueue;

    @Value("${denario.rabbitmq.routing-keys.transaction-created}")
    private String transactionCreatedRoutingKey;

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(transactionExchange, true, false);
    }

    @Bean
    public Queue transactionCreatedQueue() {
        return QueueBuilder.durable(transactionCreatedQueue).build();
    }

    @Bean
    public Binding transactionCreatedBinding() {
        return BindingBuilder
                .bind(transactionCreatedQueue())
                .to(transactionExchange())
                .with(transactionCreatedRoutingKey);
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
}
