package com.thmanyah.services.cmsdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class CmsDiscoveryApplication {

  public static void main(String[] args) {
    SpringApplication.run(CmsDiscoveryApplication.class, args);
  }
}
