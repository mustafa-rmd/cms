package com.thmanyah.services.cms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for background import job processing
 */
@Configuration
@Slf4j
public class ImportJobQueueConfig {

    @Value("${cms.import.rabbitmq.exchange:cms.import.exchange}")
    private String importExchange;

    @Value("${cms.import.rabbitmq.queue:cms.import.queue}")
    private String importQueue;

    @Value("${cms.import.rabbitmq.routing-key:import.job}")
    private String importRoutingKey;

    @Value("${cms.import.rabbitmq.dlq:cms.import.dlq}")
    private String importDlq;

    @Value("${cms.import.rabbitmq.retry-queue:cms.import.retry}")
    private String retryQueue;

    // Exchange for import jobs
    @Bean
    public DirectExchange importJobExchange() {
        log.info("Creating import job exchange: {}", importExchange);
        return ExchangeBuilder.directExchange(importExchange)
                .durable(true)
                .build();
    }

    // Main import job queue
    @Bean
    public Queue importJobQueue() {
        return QueueBuilder.durable(importQueue)
                .withArgument("x-dead-letter-exchange", importExchange)
                .withArgument("x-dead-letter-routing-key", "import.failed")
                .withArgument("x-message-ttl", 1800000) // 30 minutes TTL
                .build();
    }

    // Dead letter queue for permanently failed jobs
    @Bean
    public Queue importJobDlq() {
        return QueueBuilder.durable(importDlq).build();
    }

    // Retry queue for temporary failures
    @Bean
    public Queue importJobRetryQueue() {
        return QueueBuilder.durable(retryQueue)
                .withArgument("x-dead-letter-exchange", importExchange)
                .withArgument("x-dead-letter-routing-key", importRoutingKey)
                .withArgument("x-message-ttl", 60000) // 1 minute delay before retry
                .build();
    }

    // Bindings
    @Bean
    public Binding importJobBinding() {
        return BindingBuilder.bind(importJobQueue())
                .to(importJobExchange())
                .with(importRoutingKey);
    }

    @Bean
    public Binding importJobDlqBinding() {
        return BindingBuilder.bind(importJobDlq())
                .to(importJobExchange())
                .with("import.failed");
    }

    @Bean
    public Binding importJobRetryBinding() {
        return BindingBuilder.bind(importJobRetryQueue())
                .to(importJobExchange())
                .with("import.retry");
    }

    // Listener container factory for import jobs
    @Bean("importJobListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory importJobListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setDefaultRequeueRejected(false); // Send to DLQ instead of requeue
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        
        // Concurrency settings
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        
        return factory;
    }

    // Template for sending import jobs
    @Bean("importJobRabbitTemplate")
    public RabbitTemplate importJobRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setExchange(importExchange);
        template.setRoutingKey(importRoutingKey);
        return template;
    }
}
