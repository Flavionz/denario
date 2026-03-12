package com.denario.account.config;

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

    @Value("${denario.rabbitmq.exchanges.account}")
    private String accountExchange;

    @Value("${denario.rabbitmq.queues.account-created}")
    private String accountCreatedQueue;

    @Value("${denario.rabbitmq.queues.account-updated}")
    private String accountUpdatedQueue;

    @Value("${denario.rabbitmq.routing-keys.account-created}")
    private String accountCreatedRoutingKey;

    @Value("${denario.rabbitmq.routing-keys.account-updated}")
    private String accountUpdatedRoutingKey;

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(accountExchange, true, false);
    }

    @Bean
    public Queue accountCreatedQueue() {
        return QueueBuilder.durable(accountCreatedQueue).build();
    }

    @Bean
    public Queue accountUpdatedQueue() {
        return QueueBuilder.durable(accountUpdatedQueue).build();
    }

    @Bean
    public Binding accountCreatedBinding() {
        return BindingBuilder
                .bind(accountCreatedQueue())
                .to(accountExchange())
                .with(accountCreatedRoutingKey);
    }

    @Bean
    public Binding accountUpdatedBinding() {
        return BindingBuilder
                .bind(accountUpdatedQueue())
                .to(accountExchange())
                .with(accountUpdatedRoutingKey);
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