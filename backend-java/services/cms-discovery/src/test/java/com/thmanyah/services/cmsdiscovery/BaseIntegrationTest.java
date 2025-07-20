package com.thmanyah.services.cmsdiscovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thmanyah.services.cmsdiscovery.config.TestContainersConfiguration;
import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.event.ShowCreatedEvent;
import com.thmanyah.services.cmsdiscovery.repository.ShowRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Base class for integration tests with TestContainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ShowRepository showRepository;

    @Autowired
    protected ElasticsearchOperations elasticsearchOperations;

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Autowired
    protected ElasticsearchContainer elasticsearchContainer;

    @Autowired
    protected RabbitMQContainer rabbitMQContainer;

    @Value("${cms-discovery.rabbitmq.exchange}")
    protected String exchangeName;

    @Value("${cms-discovery.rabbitmq.routing-key}")
    protected String routingKey;

    @Value("${cms-discovery.elasticsearch.index-name}")
    protected String indexName;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Clean up Elasticsearch data before each test
        cleanupElasticsearchData();
        
        // Wait for Elasticsearch to be ready
        waitForElasticsearch();
    }

    private void cleanupElasticsearchData() {
        try {
            showRepository.deleteAll();
            // Force refresh to ensure data is deleted immediately
            elasticsearchOperations.indexOps(ShowDocument.class).refresh();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    private void waitForElasticsearch() {
        try {
            Thread.sleep(1000); // Wait 1 second for Elasticsearch to be ready
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Helper method to create unauthenticated request (Discovery service is public)
     */
    protected io.restassured.specification.RequestSpecification publicRequest() {
        return io.restassured.RestAssured.given()
                .contentType(ContentType.JSON);
    }

    /**
     * Helper method to create a test show document
     */
    protected ShowDocument createTestShowDocument(String title, String description) {
        return createTestShowDocument(title, description, "podcast", "en");
    }

    protected ShowDocument createTestShowDocument(String title, String description, String type, String language) {
        ShowDocument document = ShowDocument.builder()
                .showId(UUID.randomUUID())
                .title(title)
                .description(description)
                .type(type)
                .language(language)
                .durationSec(3600)
                .publishedAt(LocalDate.now())
                .provider("test")
                .tags(List.of("test", "integration"))
                .categories(List.of("Technology"))
                .isPublic(true)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .createdBy("test@example.com")
                .updatedBy("test@example.com")
                .build();

        // Generate search text
        document.generateSearchText();
        
        return showRepository.save(document);
    }

    /**
     * Helper method to create a test show event
     */
    protected ShowCreatedEvent createTestShowEvent(String title, String description) {
        return createTestShowEvent(title, description, "podcast", "en");
    }

    protected ShowCreatedEvent createTestShowEvent(String title, String description, String type, String language) {
        return ShowCreatedEvent.builder()
                .showId(UUID.randomUUID())
                .title(title)
                .description(description)
                .type(type)
                .language(language)
                .durationSec(3600)
                .publishedAt(LocalDate.now())
                .provider("test")
                .tags(List.of("test", "integration"))
                .categories(List.of("Technology"))
                .isPublic(true)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .createdBy("test@example.com")
                .updatedBy("test@example.com")
                .eventId(UUID.randomUUID().toString())
                .eventType("SHOW_CREATED")
                .eventSource("cms-service")
                .eventTimestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Helper method to publish a show event via RabbitMQ
     */
    protected void publishShowEvent(ShowCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }

    /**
     * Wait for RabbitMQ message processing
     */
    protected void waitForMessageProcessing() throws InterruptedException {
        Thread.sleep(2000); // Wait 2 seconds for async processing
    }

    /**
     * Wait for Elasticsearch indexing
     */
    protected void waitForElasticsearchIndexing() throws InterruptedException {
        Thread.sleep(1000); // Wait 1 second for indexing
        // Force refresh to make documents searchable immediately
        elasticsearchOperations.indexOps(ShowDocument.class).refresh();
    }

    /**
     * Helper method to get the total count of documents in Elasticsearch
     */
    protected long getDocumentCount() {
        return showRepository.count();
    }
}
