package com.thmanyah.services.cmsdiscovery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

  @Value("${cms-discovery.rabbitmq.exchange}")
  private String exchangeName;

  @Value("${cms-discovery.rabbitmq.queue}")
  private String queueName;

  @Value("${cms-discovery.rabbitmq.routing-key}")
  private String routingKey;

  @Value("${cms-discovery.rabbitmq.dead-letter-queue}")
  private String deadLetterQueueName;

  /** Exchange for CMS events */
  @Bean
  public TopicExchange cmsEventsExchange() {
    return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
  }

  /** Main queue for show events */
  @Bean
  public Queue showEventsQueue() {
    return QueueBuilder.durable(queueName)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", deadLetterQueueName)
        .withArgument("x-message-ttl", 300000) // 5 minutes TTL
        .build();
  }

  /** Dead letter queue for failed messages */
  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder.durable(deadLetterQueueName).build();
  }

  /** Binding for show events */
  @Bean
  public Binding showEventsBinding() {
    return BindingBuilder.bind(showEventsQueue()).to(cmsEventsExchange()).with(routingKey);
  }

  /** JSON message converter */
  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /** RabbitTemplate with JSON converter */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }

  /** Listener container factory with JSON converter */
  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter());
    factory.setDefaultRequeueRejected(false); // Send failed messages to DLQ
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

    // Retry configuration
    factory.setAdviceChain(
        org.springframework.retry.interceptor.RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .build());

    return factory;
  }

  /** Initialize queues and exchanges on startup */
  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    return new org.springframework.amqp.rabbit.core.RabbitAdmin(connectionFactory);
  }
}
