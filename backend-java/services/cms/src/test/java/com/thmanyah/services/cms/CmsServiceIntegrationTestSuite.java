package com.thmanyah.services.cms;

import com.thmanyah.services.cms.controllers.AsyncImportControllerIntegrationTest;
import com.thmanyah.services.cms.controllers.AuthControllerIntegrationTest;
import com.thmanyah.services.cms.controllers.ShowControllerIntegrationTest;
import com.thmanyah.services.cms.repositories.ShowRepositoryIntegrationTest;
import com.thmanyah.services.cms.repositories.UserRepositoryIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Integration test suite for CMS Service
 *
 * <p>This suite runs all integration tests with TestContainers for: - PostgreSQL database -
 * RabbitMQ message broker - Complete application context
 *
 * <p>Run with: mvn test -Dtest=CmsServiceIntegrationTestSuite
 */
@Suite
@SuiteDisplayName("CMS Service Integration Test Suite")
@DisplayName("CMS Service Integration Tests with TestContainers")
@SelectClasses({
  // Controller Integration Tests
  AuthControllerIntegrationTest.class,
  ShowControllerIntegrationTest.class,
  AsyncImportControllerIntegrationTest.class,

  // Repository Integration Tests
  ShowRepositoryIntegrationTest.class,
  UserRepositoryIntegrationTest.class
})
public class CmsServiceIntegrationTestSuite {
  // Test suite configuration class
  // All tests will use the same TestContainers instances for efficiency
}
