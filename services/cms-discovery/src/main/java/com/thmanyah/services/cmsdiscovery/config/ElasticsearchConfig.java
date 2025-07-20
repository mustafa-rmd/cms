package com.thmanyah.services.cmsdiscovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.RefreshPolicy;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris}")
  private String elasticsearchUris;

  @Value("${spring.elasticsearch.username:}")
  private String username;

  @Value("${spring.elasticsearch.password:}")
  private String password;

  @Value("${spring.elasticsearch.connection-timeout:10s}")
  private String connectionTimeout;

  @Value("${spring.elasticsearch.socket-timeout:30s}")
  private String socketTimeout;

  @Value("${cms-discovery.elasticsearch.index-name}")
  private String indexName;

  @Override
  public ClientConfiguration clientConfiguration() {
    var builder =
        ClientConfiguration.builder()
            .connectedTo(elasticsearchUris.replace("http://", "").replace("https://", ""))
            .withConnectTimeout(java.time.Duration.parse("PT" + connectionTimeout.toUpperCase()))
            .withSocketTimeout(java.time.Duration.parse("PT" + socketTimeout.toUpperCase()));

    // Add authentication if provided
    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
      builder.withBasicAuth(username, password);
    }

    return builder.build();
  }

  @Bean
  public String elasticsearchIndexName() {
    return indexName;
  }

  @Bean
  public RefreshPolicy elasticsearchRefreshPolicy(
      @Value("${cms-discovery.elasticsearch.refresh-policy}") String refreshPolicy) {
    return switch (refreshPolicy.toLowerCase()) {
      case "immediate" -> RefreshPolicy.IMMEDIATE;
      case "wait_for" -> RefreshPolicy.WAIT_UNTIL;
      default -> RefreshPolicy.NONE;
    };
  }
}
