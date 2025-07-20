# CMS Service Testing Guide

This guide covers comprehensive integration testing for the CMS service using TestContainers.

## 🧪 Test Architecture

### Test Infrastructure
- **TestContainers**: Real PostgreSQL and RabbitMQ containers
- **Spring Boot Test**: Full application context
- **REST Assured**: API endpoint testing
- **AssertJ**: Fluent assertions
- **JUnit 5**: Test framework

### Container Setup
- **PostgreSQL 17.5**: Database with real schema
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
cd backend-java/services/cms
mvn clean test
```

### Run Integration Tests Only
```bash
mvn clean verify -Dtest=*IntegrationTest
```

### Run Test Suite
```bash
mvn test -Dtest=CmsServiceIntegrationTestSuite
```

### Run Specific Test Categories
```bash
# Authentication tests
mvn test -Dtest=AuthControllerIntegrationTest

# Show management tests
mvn test -Dtest=ShowControllerIntegrationTest

# Async import tests
mvn test -Dtest=AsyncImportControllerIntegrationTest

# Repository tests
mvn test -Dtest=ShowRepositoryIntegrationTest
mvn test -Dtest=UserRepositoryIntegrationTest
```

## 📋 Test Coverage

### Authentication Tests (10 tests)
- ✅ Login with admin/editor credentials
- ✅ Login failure scenarios
- ✅ Token refresh functionality
- ✅ Logout operations
- ✅ User info retrieval
- ✅ Authentication validation

### Show Management Tests (12 tests)
- ✅ Create shows (admin/editor roles)
- ✅ CRUD operations with validation
- ✅ Pagination and filtering
- ✅ Search functionality
- ✅ Type and language filtering
- ✅ Authorization checks

### Async Import Tests (12 tests)
- ✅ Start async import jobs
- ✅ Job status tracking
- ✅ User-specific job queries
- ✅ Admin-only operations
- ✅ Job cancellation and retry
- ✅ Statistics and providers
- ✅ Request validation

### Repository Tests (20 tests)
#### Show Repository (12 tests)
- ✅ CRUD operations
- ✅ Query methods with pagination
- ✅ Filtering by type, language, provider
- ✅ Date range queries
- ✅ Count operations
- ✅ Distinct value queries

#### User Repository (8 tests)
- ✅ User CRUD operations
- ✅ Email-based queries (case-insensitive)
- ✅ Role-based filtering
- ✅ Active user queries
- ✅ DTO projections
- ✅ Unique constraint validation

## 🔧 Test Configuration

### Environment Variables
Tests use TestContainers dynamic configuration:

```yaml
# PostgreSQL (auto-configured)
spring.datasource.url: jdbc:postgresql://localhost:random-port/cms_test_db
spring.datasource.username: test_user
spring.datasource.password: test_password

# RabbitMQ (auto-configured)
spring.rabbitmq.host: localhost
spring.rabbitmq.port: random-port
spring.rabbitmq.username: test_user
spring.rabbitmq.password: test_password

# Test-specific
cms.admin.email: test-admin@thmanyah.io
cms.admin.password: test-admin-password
cms.jwt.secret: test-secret-key-for-testing...
```

### Test Data Management
- **Automatic Cleanup**: `@Transactional` rollback
- **Test Users**: Admin and Editor created per test
- **JWT Tokens**: Real authentication flow
- **Database Schema**: Flyway migrations applied

## 🐳 TestContainers Details

### Container Images
- **PostgreSQL**: `postgres:17.5`
- **RabbitMQ**: `rabbitmq:3.13-management`

### Performance Optimizations
- **Container Reuse**: `withReuse(true)`
- **Shared Configuration**: Single setup per test class
- **Efficient Cleanup**: Transaction rollback vs full reset

### Resource Requirements
- **Memory**: ~1.5GB RAM for containers
- **Disk**: ~500MB for images
- **Network**: Random ports to avoid conflicts

## 🚨 Troubleshooting

### Common Issues

#### Docker Not Running
```bash
# Start Docker
sudo systemctl start docker  # Linux
# or start Docker Desktop
```

#### Port Conflicts
```bash
# TestContainers uses random ports automatically
# If issues persist, restart Docker
docker system prune -f
```

#### Memory Issues
```bash
# Increase Docker memory limit
# Docker Desktop: Settings > Resources > Memory (4GB+)
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
1. **Use descriptive names**: `shouldCreateShowSuccessfullyWithAdminCredentials`
2. **Follow AAA pattern**: Arrange, Act, Assert
3. **Test edge cases**: Invalid inputs, authorization failures
4. **Use helper methods**: Consistent test data creation
5. **Verify side effects**: Database state, message publishing

### Performance
1. **Reuse containers**: Significant startup time savings
2. **Minimize test data**: Create only what's needed
3. **Use transactions**: Fast cleanup via rollback
4. **Parallel execution**: Configure for CI/CD

### Maintenance
1. **Keep tests updated**: Sync with API changes
2. **Monitor duration**: Optimize slow tests
3. **Regular cleanup**: Remove obsolete tests
4. **Documentation**: Update this guide

## 🔗 Key Features Tested

### Authentication & Authorization
- JWT token generation and validation
- Role-based access control (ADMIN/EDITOR)
- Password encoding and verification
- Token refresh mechanism

### Show Management
- CRUD operations with validation
- Pagination and sorting
- Search and filtering
- Audit fields (createdBy, updatedBy)

### Async Import System
- RabbitMQ job queuing
- Job status tracking
- Progress monitoring
- Error handling and retry logic

### Database Operations
- JPA entity relationships
- Custom query methods
- DTO projections
- Constraint validation

### CORS & Security
- Cross-origin request handling
- Authentication headers
- Error response formatting

## 📈 Continuous Integration

### GitHub Actions Example
```yaml
- name: Run Integration Tests
  run: |
    cd backend-java/services/cms
    mvn clean verify
  env:
    SPRING_PROFILES_ACTIVE: test
```

### Test Execution Strategy
1. **Fast Feedback**: Unit tests first
2. **Integration Tests**: Full system validation
3. **Test Suites**: Comprehensive regression testing

The TestContainers integration tests provide confidence in the CMS service's reliability with real infrastructure dependencies, ensuring production-like testing scenarios.
