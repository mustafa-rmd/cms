package com.thmanyah.services.cmsdiscovery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test to verify TestContainers setup works for CMS Discovery service
 */
@Testcontainers
@DisplayName("Simple TestContainers Test for CMS Discovery")
public class SimpleTestContainersTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("elasticsearch:7.17.15")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management")
            .withUser("test_user", "test_password")
            .withVhost("/");

    @Test
    @DisplayName("Should start Elasticsearch container")
    void shouldStartElasticsearchContainer() {
        assertThat(elasticsearch.isRunning()).isTrue();
        assertThat(elasticsearch.getHttpHostAddress()).contains("localhost:");
        assertThat(elasticsearch.getMappedPort(9200)).isGreaterThan(1024);
    }

    @Test
    @DisplayName("Should start RabbitMQ container")
    void shouldStartRabbitMQContainer() {
        assertThat(rabbitmq.isRunning()).isTrue();
        assertThat(rabbitmq.getAmqpUrl()).contains("amqp://");
        assertThat(rabbitmq.getAdminUsername()).isEqualTo("guest");
        assertThat(rabbitmq.getAdminPassword()).isEqualTo("guest");
    }

    @Test
    @DisplayName("Should connect to Elasticsearch")
    void shouldConnectToElasticsearch() throws Exception {
        String elasticsearchUrl = "http://" + elasticsearch.getHttpHostAddress();

        // Test basic HTTP connection to Elasticsearch
        java.net.URL url = new java.net.URL(elasticsearchUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        assertThat(responseCode).isEqualTo(200);

        connection.disconnect();
    }

    @Test
    @DisplayName("Should have containers on different ports")
    void shouldHaveContainersOnDifferentPorts() {
        int elasticsearchPort = elasticsearch.getMappedPort(9200);
        int rabbitmqPort = rabbitmq.getMappedPort(5672);
        
        assertThat(elasticsearchPort).isNotEqualTo(rabbitmqPort);
        assertThat(elasticsearchPort).isGreaterThan(1024);
        assertThat(rabbitmqPort).isGreaterThan(1024);
    }

    @Test
    @DisplayName("Should verify Elasticsearch cluster health")
    void shouldVerifyElasticsearchClusterHealth() throws Exception {
        String healthUrl = "http://" + elasticsearch.getHttpHostAddress() + "/_cluster/health";

        java.net.URL url = new java.net.URL(healthUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        assertThat(responseCode).isEqualTo(200);

        // Read response to verify cluster is healthy
        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getInputStream()));
        String response = reader.lines()
                .collect(java.util.stream.Collectors.joining("\n"));
        reader.close();
        connection.disconnect();

        assertThat(response).contains("cluster_name");
        assertThat(response).contains("status");
    }

    @Test
    @DisplayName("Should verify RabbitMQ management API")
    void shouldVerifyRabbitMQManagementApi() throws Exception {
        String managementUrl = "http://" + rabbitmq.getHost() + ":" + rabbitmq.getMappedPort(15672) + "/api/overview";
        
        java.net.URL url = new java.net.URL(managementUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        // Add basic auth for RabbitMQ management API
        String auth = rabbitmq.getAdminUsername() + ":" + rabbitmq.getAdminPassword();
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
        
        int responseCode = connection.getResponseCode();
        assertThat(responseCode).isEqualTo(200);
        
        connection.disconnect();
    }
}
