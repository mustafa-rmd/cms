package com.thmanyah.services.cms;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Simple test to verify TestContainers setup works */
@Testcontainers
@DisplayName("Simple TestContainers Test")
public class SimpleTestContainersTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17.5")
          .withDatabaseName("cms_test_db")
          .withUsername("test_user")
          .withPassword("test_password");

  @Container
  static RabbitMQContainer rabbitmq =
      new RabbitMQContainer("rabbitmq:3.13-management")
          .withUser("test_user", "test_password")
          .withVhost("/");

  @Test
  @DisplayName("Should start PostgreSQL container")
  void shouldStartPostgreSQLContainer() {
    assertThat(postgres.isRunning()).isTrue();
    assertThat(postgres.getJdbcUrl()).contains("postgresql");
    assertThat(postgres.getDatabaseName()).isEqualTo("cms_test_db");
    assertThat(postgres.getUsername()).isEqualTo("test_user");
    assertThat(postgres.getPassword()).isEqualTo("test_password");
  }

  @Test
  @DisplayName("Should start RabbitMQ container")
  void shouldStartRabbitMQContainer() {
    assertThat(rabbitmq.isRunning()).isTrue();
    assertThat(rabbitmq.getAmqpUrl()).contains("amqp");
    // RabbitMQ container uses default guest/guest credentials
    assertThat(rabbitmq.getAdminUsername()).isEqualTo("guest");
    assertThat(rabbitmq.getAdminPassword()).isEqualTo("guest");
  }

  @Test
  @DisplayName("Should connect to PostgreSQL database")
  void shouldConnectToPostgreSQLDatabase() throws Exception {
    String jdbcUrl = postgres.getJdbcUrl();
    String username = postgres.getUsername();
    String password = postgres.getPassword();

    // Test basic connection
    try (var connection = java.sql.DriverManager.getConnection(jdbcUrl, username, password)) {
      assertThat(connection.isValid(5)).isTrue();

      // Test database operations
      try (var statement = connection.createStatement()) {
        var resultSet = statement.executeQuery("SELECT version()");
        assertThat(resultSet.next()).isTrue();
        String version = resultSet.getString(1);
        assertThat(version).contains("PostgreSQL");
      }
    }
  }

  @Test
  @DisplayName("Should have containers on different ports")
  void shouldHaveContainersOnDifferentPorts() {
    int postgresPort = postgres.getMappedPort(5432);
    int rabbitmqPort = rabbitmq.getMappedPort(5672);

    assertThat(postgresPort).isNotEqualTo(rabbitmqPort);
    assertThat(postgresPort).isGreaterThan(1024);
    assertThat(rabbitmqPort).isGreaterThan(1024);
  }
}
