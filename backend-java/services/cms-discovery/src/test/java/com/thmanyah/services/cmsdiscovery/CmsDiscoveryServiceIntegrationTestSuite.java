package com.thmanyah.services.cmsdiscovery;

import com.thmanyah.services.cmsdiscovery.controllers.SearchControllerIntegrationTest;
import com.thmanyah.services.cmsdiscovery.repositories.ShowRepositoryIntegrationTest;
import com.thmanyah.services.cmsdiscovery.services.ShowEventConsumerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Integration test suite for CMS Discovery Service
 * 
 * This suite runs all integration tests with TestContainers for:
 * - Elasticsearch search engine
 * - RabbitMQ message broker
 * - Complete application context
 * 
 * Run with: mvn test -Dtest=CmsDiscoveryServiceIntegrationTestSuite
 */
@Suite
@SuiteDisplayName("CMS Discovery Service Integration Test Suite")
@DisplayName("CMS Discovery Service Integration Tests with TestContainers")
@SelectClasses({
    // Controller Integration Tests
    SearchControllerIntegrationTest.class,
    
    // Repository Integration Tests
    ShowRepositoryIntegrationTest.class,
    
    // Service Integration Tests
    ShowEventConsumerIntegrationTest.class
})
public class CmsDiscoveryServiceIntegrationTestSuite {
    // Test suite configuration class
    // All tests will use the same TestContainers instances for efficiency
}
