# CMS Discovery Service Testing Guide

This guide covers comprehensive integration testing for the CMS Discovery service using TestContainers with Elasticsearch and RabbitMQ.

## 🧪 Test Architecture

### Test Infrastructure
- **TestContainers**: Real Elasticsearch and RabbitMQ containers
- **Spring Boot Test**: Full application context
- **REST Assured**: API endpoint testing
- **AssertJ**: Fluent assertions
- **JUnit 5**: Test framework

### Container Setup
- **Elasticsearch 8.11.0**: Search engine with real indexing
- **RabbitMQ 3.13**: Message broker with management UI
- **Container Reuse**: Optimized for performance
- **Automatic Cleanup**: Data cleaned between tests

## 🚀 Running Tests

### Prerequisites
- Docker installed and running
- Java 17+
- Maven 3.9+

### Run All Tests
```bash
cd backend-java/services/cms-discovery
mvn clean test
```

### Run Integration Tests Only
```bash
mvn clean verify -Dtest=*IntegrationTest
```

### Run Test Suite
```bash
mvn test -Dtest=CmsDiscoveryServiceIntegrationTestSuite
```

### Run Specific Test Categories
```bash
# Search API tests
mvn test -Dtest=SearchControllerIntegrationTest

# Elasticsearch repository tests
mvn test -Dtest=ShowRepositoryIntegrationTest

# RabbitMQ event consumer tests
mvn test -Dtest=ShowEventConsumerIntegrationTest

# Container verification tests
mvn test -Dtest=SimpleTestContainersTest
```

## 📋 Test Coverage

### Search Controller Tests (18 tests)
- ✅ Basic search functionality
- ✅ Query-based search with filters
- ✅ Type and language filtering
- ✅ Pagination and sorting
- ✅ Advanced search with POST requests
- ✅ Show retrieval by ID
- ✅ Recent shows endpoint
- ✅ CORS handling
- ✅ Parameter validation
- ✅ Fuzzy search and highlighting

### Repository Tests (14 tests)
- ✅ CRUD operations with Elasticsearch
- ✅ Query methods with pagination
- ✅ Full-text search functionality
- ✅ Filtering by type, language, provider
- ✅ Date range queries
- ✅ Duration-based filtering
- ✅ Public/active show filtering
- ✅ Search text generation
- ✅ Document counting and deletion

### Event Consumer Tests (10 tests)
- ✅ RabbitMQ message consumption
- ✅ Show event processing and indexing
- ✅ Multiple event handling
- ✅ Duplicate event detection
- ✅ Complete field mapping
- ✅ Minimal field handling
- ✅ Different show types and languages
- ✅ Tags and categories processing
- ✅ Private/inactive show handling

### Container Verification Tests (6 tests)
- ✅ Elasticsearch container startup
- ✅ RabbitMQ container startup
- ✅ Container connectivity
- ✅ Port isolation
- ✅ Cluster health verification
- ✅ Management API access

## 🔧 Test Configuration

### Environment Variables
Tests use TestContainers dynamic configuration:

```yaml
# Elasticsearch (auto-configured)
spring.elasticsearch.uris: http://localhost:random-port
spring.elasticsearch.username: ""
spring.elasticsearch.password: ""

# RabbitMQ (auto-configured)
spring.rabbitmq.host: localhost
spring.rabbitmq.port: random-port
spring.rabbitmq.username: guest
spring.rabbitmq.password: guest

# Test-specific
cms-discovery.elasticsearch.index-name: test-cms-shows
cms-discovery.elasticsearch.refresh-policy: immediate
cms-discovery.rabbitmq.exchange: test.cms.events
cms-discovery.rabbitmq.queue: test.cms-discovery.shows
```

### Test Data Management
- **Automatic Cleanup**: Elasticsearch index cleared between tests
- **Immediate Refresh**: Documents available for search immediately
- **Event Publishing**: Real RabbitMQ message flow
- **Search Indexing**: Full Elasticsearch functionality

## 🐳 TestContainers Details

### Container Images
- **Elasticsearch**: `docker.elastic.co/elasticsearch/elasticsearch:8.11.0`
- **RabbitMQ**: `rabbitmq:3.13-management`

### Performance Optimizations
- **Container Reuse**: `withReuse(true)`
- **Memory Limits**: 512MB for Elasticsearch
- **Single Node**: Simplified Elasticsearch cluster
- **Security Disabled**: Faster startup for tests

### Resource Requirements
- **Memory**: ~2GB RAM for containers
- **Disk**: ~1GB for images
- **Network**: Random ports to avoid conflicts

## 🚨 Troubleshooting

### Common Issues

#### Docker Not Running
```bash
# Start Docker
sudo systemctl start docker  # Linux
# or start Docker Desktop
```

#### Elasticsearch Memory Issues
```bash
# Increase Docker memory limit
# Docker Desktop: Settings > Resources > Memory (4GB+)
```

#### Port Conflicts
```bash
# TestContainers uses random ports automatically
# If issues persist, restart Docker
docker system prune -f
```

#### Test Failures
```bash
# Run with debug logging
mvn test -Dlogging.level.com.thmanyah=DEBUG

# Check container logs
docker logs <container-id>
```

### Debug Mode
```bash
# Enable TestContainers debug
mvn test -Dlogging.level.org.testcontainers=DEBUG

# Enable Elasticsearch debug
mvn test -Dlogging.level.org.elasticsearch=DEBUG
```

## 📊 Test Reports

### Surefire Reports
```bash
# After running tests
target/surefire-reports/
target/site/surefire-report.html
```

### Coverage Reports
```bash
# If JaCoCo is configured
mvn jacoco:report
# View: target/site/jacoco/index.html
```

## 🎯 Best Practices

### Writing Tests
1. **Use descriptive names**: `shouldSearchShowsByQueryAndFilterByType`
2. **Follow AAA pattern**: Arrange, Act, Assert
3. **Test edge cases**: Empty results, invalid parameters
4. **Use helper methods**: Consistent test data creation
5. **Verify side effects**: Elasticsearch indexing, message consumption

### Performance
1. **Reuse containers**: Significant startup time savings
2. **Minimize test data**: Create only what's needed
3. **Use immediate refresh**: Fast Elasticsearch indexing
4. **Parallel execution**: Configure for CI/CD

### Maintenance
1. **Keep tests updated**: Sync with API changes
2. **Monitor duration**: Optimize slow tests
3. **Regular cleanup**: Remove obsolete tests
4. **Documentation**: Update this guide

## 🔗 Key Features Tested

### Search Functionality
- Full-text search with Elasticsearch
- Advanced filtering and sorting
- Pagination and result limiting
- Fuzzy search and highlighting
- Multi-field queries

### Event-Driven Architecture
- RabbitMQ message consumption
- Real-time content indexing
- Event deduplication
- Error handling and retry logic

### Elasticsearch Operations
- Document CRUD operations
- Index management
- Query optimization
- Search text generation
- Field mapping validation

### Public API
- RESTful endpoint testing
- CORS configuration
- Parameter validation
- Error response handling

## 📈 Continuous Integration

### GitHub Actions Example
```yaml
- name: Run Discovery Integration Tests
  run: |
    cd backend-java/services/cms-discovery
    mvn clean verify
  env:
    SPRING_PROFILES_ACTIVE: test
```

### Test Execution Strategy
1. **Container Verification**: Basic connectivity tests
2. **Repository Tests**: Elasticsearch functionality
3. **Event Processing**: RabbitMQ integration
4. **API Tests**: Complete endpoint validation

The TestContainers integration tests provide confidence in the CMS Discovery service's search capabilities with real Elasticsearch and RabbitMQ infrastructure, ensuring production-like testing scenarios.
