package com.thmanyah.services.cmsdiscovery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * TestContainers configuration for CMS Discovery service integration tests
 */
@TestConfiguration
@Testcontainers
@Slf4j
public class TestContainersConfiguration {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withReuse(true);

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management")
            .withReuse(true);

    static {
        // Start containers before Spring context loads
        elasticsearch.start();
        rabbitmq.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Elasticsearch configuration
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
        registry.add("spring.elasticsearch.username", () -> "");
        registry.add("spring.elasticsearch.password", () -> "");
        registry.add("spring.elasticsearch.connection-timeout", () -> "10s");
        registry.add("spring.elasticsearch.socket-timeout", () -> "30s");
        
        // RabbitMQ configuration
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
        registry.add("spring.rabbitmq.virtual-host", () -> "/");
        
        // CMS Discovery specific configuration for tests
        registry.add("cms-discovery.elasticsearch.index-name", () -> "test-cms-shows");
        registry.add("cms-discovery.elasticsearch.refresh-policy", () -> "immediate");
        registry.add("cms-discovery.elasticsearch.batch-size", () -> "10");
        
        // RabbitMQ event configuration for tests
        registry.add("cms-discovery.rabbitmq.exchange", () -> "test.cms.events");
        registry.add("cms-discovery.rabbitmq.queue", () -> "test.cms-discovery.shows");
        registry.add("cms-discovery.rabbitmq.routing-key", () -> "test.show.created");
        registry.add("cms-discovery.rabbitmq.dead-letter-queue", () -> "test.cms-discovery.shows.dlq");
        
        // Search configuration for tests
        registry.add("cms-discovery.search.default-page-size", () -> "10");
        registry.add("cms-discovery.search.max-page-size", () -> "50");
        registry.add("cms-discovery.search.highlight-enabled", () -> "true");
        registry.add("cms-discovery.search.fuzzy-enabled", () -> "true");
        
        // CORS configuration for tests
        registry.add("cms-discovery.cors.allowed-origins", () -> "http://localhost:3000,http://localhost:4200");
        registry.add("cms-discovery.cors.allowed-methods", () -> "GET,POST,OPTIONS");
        registry.add("cms-discovery.cors.allowed-headers", () -> "*");
        registry.add("cms-discovery.cors.allow-credentials", () -> "false");
        registry.add("cms-discovery.cors.max-age", () -> "3600");
        
        log.info("TestContainers configured:");
        log.info("Elasticsearch URL: {}", elasticsearch.getHttpHostAddress());
        log.info("RabbitMQ URL: amqp://{}:{}", rabbitmq.getHost(), rabbitmq.getAmqpPort());
    }

    @Bean
    public ElasticsearchContainer elasticsearchContainer() {
        return elasticsearch;
    }

    @Bean
    public RabbitMQContainer rabbitMQContainer() {
        return rabbitmq;
    }
}
