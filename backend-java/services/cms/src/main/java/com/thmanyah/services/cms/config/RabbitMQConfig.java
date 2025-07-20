package com.thmanyah.services.cms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

  @Value("${cms.events.rabbitmq.exchange}")
  private String exchangeName;

  /** Exchange for CMS events */
  @Bean
  public TopicExchange cmsEventsExchange() {
    log.info("Creating CMS events exchange: {}", exchangeName);
    return new TopicExchange(exchangeName, true, false);
  }

  /** JSON message converter for RabbitMQ */
  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /** Configure RabbitTemplate with JSON converter */
  @Bean
  public RabbitTemplate rabbitTemplate(
      org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    template.setExchange(exchangeName);
    return template;
  }
}
