package com.thmanyah.services.cms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** TestContainers configuration for CMS service integration tests */
@TestConfiguration
@Testcontainers
@Slf4j
public class TestContainersConfiguration {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17.5")
          .withDatabaseName("cms_test_db")
          .withUsername("test_user")
          .withPassword("test_password")
          .withReuse(true);

  @Container
  static RabbitMQContainer rabbitmq =
      new RabbitMQContainer("rabbitmq:3.13-management").withReuse(true);

  static {
    // Start containers before Spring context loads
    postgres.start();
    rabbitmq.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // PostgreSQL configuration
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    // JPA/Hibernate configuration for tests
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.show-sql", () -> "true");
    registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
    registry.add(
        "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");

    // Flyway configuration for tests
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add("spring.flyway.clean-disabled", () -> "false");

    // RabbitMQ configuration
    registry.add("spring.rabbitmq.host", rabbitmq::getHost);
    registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
    registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
    registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    registry.add("spring.rabbitmq.virtual-host", () -> "/");

    // Test-specific configurations
    registry.add("cms.admin.email", () -> "test-admin@thmanyah.io");
    registry.add("cms.admin.password", () -> "test-admin-password");

    // JWT configuration for tests
    registry.add(
        "cms.jwt.secret",
        () ->
            "test-secret-key-for-testing-purposes-only-not-for-production-use-must-be-at-least-256-bits");
    registry.add("cms.jwt.access-token-expiration", () -> "3600000"); // 1 hour
    registry.add("cms.jwt.refresh-token-expiration", () -> "86400000"); // 24 hours

    // Import job configuration for tests
    registry.add("cms.import.rabbitmq.exchange", () -> "test.cms.import.exchange");
    registry.add("cms.import.rabbitmq.queue", () -> "test.cms.import.queue");
    registry.add("cms.import.rabbitmq.routing-key", () -> "test.import.job");
    registry.add("cms.import.rabbitmq.dlq", () -> "test.cms.import.dlq");
    registry.add("cms.import.rabbitmq.retry-queue", () -> "test.cms.import.retry");

    // Event publishing configuration for tests
    registry.add("cms.events.rabbitmq.exchange", () -> "test.cms.events");
    registry.add("cms.events.rabbitmq.routing-key.show-created", () -> "test.show.created");
    registry.add("cms.events.rabbitmq.routing-key.show-updated", () -> "test.show.updated");
    registry.add("cms.events.rabbitmq.routing-key.show-deleted", () -> "test.show.deleted");

    // Disable external provider calls in tests
    registry.add("cms.external-providers.youtube.enabled", () -> "false");
    registry.add("cms.external-providers.vimeo.enabled", () -> "false");
    registry.add("cms.external-providers.mock.enabled", () -> "true");

    // CORS configuration for tests
    registry.add("cms.cors.allowed-origins", () -> "http://localhost:3000,http://localhost:4200");
    registry.add("cms.cors.allowed-methods", () -> "GET,POST,PUT,DELETE,PATCH,OPTIONS");
    registry.add("cms.cors.allowed-headers", () -> "*");
    registry.add("cms.cors.allow-credentials", () -> "true");
    registry.add("cms.cors.max-age", () -> "3600");

    log.info("TestContainers configured:");
    log.info("PostgreSQL URL: {}", postgres.getJdbcUrl());
    log.info("RabbitMQ URL: amqp://{}:{}", rabbitmq.getHost(), rabbitmq.getAmqpPort());
  }

  @Bean
  public PostgreSQLContainer<?> postgresContainer() {
    return postgres;
  }

  @Bean
  public RabbitMQContainer rabbitMQContainer() {
    return rabbitmq;
  }
}
