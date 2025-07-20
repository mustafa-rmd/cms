# Testing Guide for CMS Services

This guide covers comprehensive testing for both CMS and CMS Discovery services using TestContainers for integration testing.

## 🧪 Test Architecture

### Test Types
- **Unit Tests**: Fast, isolated tests for individual components
- **Integration Tests**: Full application context with TestContainers
- **Repository Tests**: Database/Elasticsearch integration tests
- **Controller Tests**: REST API endpoint tests with authentication

### TestContainers Infrastructure
- **PostgreSQL**: Database for CMS service
- **Elasticsearch**: Search engine for CMS Discovery service
- **RabbitMQ**: Message broker for both services
- **Automatic cleanup**: Containers reused across tests for performance

## 🚀 Running Tests

### Prerequisites
- Docker installed and running
- Java 21+
- Maven 3.9+

### CMS Service Tests

#### Run All Tests
```bash
cd backend-java/services/cms
mvn clean test
```

#### Run Integration Tests Only
```bash
mvn clean verify -Dtest=*IntegrationTest
```

#### Run Test Suite
```bash
mvn test -Dtest=CmsServiceIntegrationTestSuite
```

#### Run Specific Test Categories
```bash
# Authentication tests
mvn test -Dtest=AuthControllerIntegrationTest

# Show management tests
mvn test -Dtest=ShowControllerIntegrationTest

# Async import tests
mvn test -Dtest=AsyncImportControllerIntegrationTest

# Repository tests
mvn test -Dtest=ShowRepositoryIntegrationTest
```

### CMS Discovery Service Tests

#### Run All Tests
```bash
cd backend-java/services/cms-discovery
mvn clean test
```

#### Run Integration Tests Only
```bash
mvn clean verify -Dtest=*IntegrationTest
```

#### Run Test Suite
```bash
mvn test -Dtest=CmsDiscoveryServiceIntegrationTestSuite
```

#### Run Specific Test Categories
```bash
# Search functionality tests
mvn test -Dtest=SearchControllerIntegrationTest

# Elasticsearch repository tests
mvn test -Dtest=ShowSearchRepositoryIntegrationTest
```

### Run Tests for Both Services
```bash
# From project root
cd backend-java
mvn clean test -pl services/cms,services/cms-discovery
```

## 📋 Test Coverage

### CMS Service Tests

#### Authentication (`AuthControllerIntegrationTest`)
- ✅ Login with valid admin credentials
- ✅ Login with valid editor credentials
- ✅ Login failure with invalid credentials
- ✅ Token refresh functionality
- ✅ Logout functionality
- ✅ Get current user info
- ✅ Authentication validation

#### Show Management (`ShowControllerIntegrationTest`)
- ✅ Create shows (admin/editor)
- ✅ Get shows with pagination
- ✅ Get show by ID
- ✅ Update show
- ✅ Delete show
- ✅ Search shows by title
- ✅ Filter by type and topic
- ✅ Authorization checks

#### Async Import (`AsyncImportControllerIntegrationTest`)
- ✅ Start async import jobs
- ✅ Get job status
- ✅ Get user's jobs
- ✅ Get all jobs (admin only)
- ✅ Filter jobs by status
- ✅ Get import statistics
- ✅ Get available providers
- ✅ Cancel jobs
- ✅ Retry failed jobs

#### Repository (`ShowRepositoryIntegrationTest`)
- ✅ CRUD operations
- ✅ Query methods
- ✅ Filtering and searching
- ✅ Pagination
- ✅ Custom queries

### CMS Discovery Service Tests

#### Search (`SearchControllerIntegrationTest`)
- ✅ Search shows by query
- ✅ Pagination support
- ✅ Filter by type, topic, provider
- ✅ Combined query and filters
- ✅ Fuzzy search
- ✅ Highlighted results
- ✅ CORS headers
- ✅ Error handling

#### Repository (`ShowSearchRepositoryIntegrationTest`)
- ✅ Elasticsearch operations
- ✅ Full-text search
- ✅ Filtering and aggregations
- ✅ Date range queries
- ✅ Count operations
- ✅ Bulk operations

## 🔧 Test Configuration

### Environment Variables
Tests use the following environment variables (with defaults):

#### CMS Service
```bash
# Database (overridden by TestContainers)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cms_test_db
SPRING_DATASOURCE_USERNAME=test_user
SPRING_DATASOURCE_PASSWORD=test_password

# RabbitMQ (overridden by TestContainers)
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=test_user
SPRING_RABBITMQ_PASSWORD=test_password

# Test-specific
SPRING_PROFILES_ACTIVE=test
CMS_ADMIN_EMAIL=test-admin@thmanyah.io
CMS_ADMIN_PASSWORD=test-admin-password
```

#### CMS Discovery Service
```bash
# Elasticsearch (overridden by TestContainers)
SPRING_ELASTICSEARCH_URIS=http://localhost:9200

# RabbitMQ (overridden by TestContainers)
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672

# Test-specific
SPRING_PROFILES_ACTIVE=test
CMS_DISCOVERY_ELASTICSEARCH_INDEX_NAME=test-cms-shows
```

### Test Profiles
- **test**: Main test profile with TestContainers
- **application-test.yaml**: Test-specific configuration
- **Automatic cleanup**: Data cleaned between tests

## 🐳 TestContainers Details

### Container Images
- **PostgreSQL**: `postgres:17.5`
- **Elasticsearch**: `docker.elastic.co/elasticsearch/elasticsearch:8.11.0`
- **RabbitMQ**: `rabbitmq:3.13-management`

### Container Reuse
- Containers are reused across tests for performance
- Automatic cleanup between test methods
- Shared configuration in `TestContainersConfiguration`

### Resource Requirements
- **Memory**: ~2GB RAM for all containers
- **Disk**: ~1GB for container images
- **Network**: Containers use random ports

## 🚨 Troubleshooting

### Common Issues

#### Docker Not Running
```bash
# Start Docker Desktop or Docker daemon
sudo systemctl start docker  # Linux
# or start Docker Desktop app
```

#### Port Conflicts
```bash
# TestContainers uses random ports, but if issues persist:
docker ps  # Check running containers
docker stop $(docker ps -q)  # Stop all containers
```

#### Memory Issues
```bash
# Increase Docker memory limit to 4GB+
# In Docker Desktop: Settings > Resources > Memory
```

#### Test Timeouts
```bash
# Increase timeout in test configuration
# Or run tests with more memory:
mvn test -Xmx2g
```

### Debug Mode
```bash
# Run tests with debug logging
mvn test -Dlogging.level.com.thmanyah=DEBUG -Dlogging.level.org.testcontainers=DEBUG
```

### Clean Test Environment
```bash
# Clean all test data and containers
mvn clean
docker system prune -f
```

## 📊 Test Reports

### Surefire Reports
```bash
# After running tests, reports are in:
target/surefire-reports/
target/site/surefire-report.html
```

### Coverage Reports
```bash
# Generate coverage report (if configured)
mvn jacoco:report
# View at: target/site/jacoco/index.html
```

## 🎯 Best Practices

### Writing Tests
1. **Use descriptive test names**: `shouldCreateShowSuccessfullyWithAdminCredentials`
2. **Follow AAA pattern**: Arrange, Act, Assert
3. **Clean up data**: Use `@Transactional` or manual cleanup
4. **Test edge cases**: Invalid inputs, authorization failures
5. **Use test data builders**: Create reusable test data methods

### Performance
1. **Reuse containers**: Use `@Testcontainers` with reuse
2. **Minimize data**: Create only necessary test data
3. **Parallel execution**: Configure for parallel test runs
4. **Fast feedback**: Run unit tests before integration tests

### Maintenance
1. **Keep tests updated**: Update when APIs change
2. **Monitor test duration**: Optimize slow tests
3. **Regular cleanup**: Remove obsolete tests
4. **Documentation**: Update this guide when adding new tests

## 🔗 Related Documentation

- [TestContainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [REST Assured Documentation](https://rest-assured.io/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

## 📈 Continuous Integration

### GitHub Actions / CI Pipeline
```yaml
# Example CI configuration
- name: Run Tests
  run: |
    cd backend-java
    mvn clean verify -pl services/cms,services/cms-discovery
  env:
    SPRING_PROFILES_ACTIVE: test
```

### Test Execution Order
1. Unit tests (fast feedback)
2. Integration tests (comprehensive validation)
3. Test suites (full regression testing)

The comprehensive test suite ensures reliability and maintainability of both CMS services with real infrastructure dependencies through TestContainers.
