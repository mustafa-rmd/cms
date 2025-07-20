# CMS Platform - Monorepo

A comprehensive Content Management System (CMS) platform built with Java and Spring Boot, designed for managing and discovering multimedia content including podcasts and documentaries. The platform features an event-driven microservices architecture optimized for scalability and real-time content discovery.

## 🏗️ Architecture Overview

The platform consists of two main microservices communicating through an event-driven architecture:

```
┌─────────────────┐    Events     ┌──────────────────┐    Search     ┌─────────────────┐
│   CMS Service   │──────────────▶│    RabbitMQ      │──────────────▶│ Discovery Service│
│   (Port 8078)   │               │                  │               │   (Port 8079)    │
│                 │               └──────────────────┘               │                 │
│ • Content CRUD  │                                                  │ • Public Search │
│ • Import Jobs   │               ┌──────────────────┐               │ • Elasticsearch │
│ • PostgreSQL    │               │   PostgreSQL     │               │ • Read-Only API │
│ • Admin API     │               │                  │               │                 │
└─────────────────┘               └──────────────────┘               └─────────────────┘
```

## 🚀 Services

### 1. CMS Service (`services/cms`)
**Write-optimized content management service**

- **Purpose**: Administrative interface for content management and external provider imports
- **Port**: 8078
- **Database**: PostgreSQL with optimized write-heavy schema
- **Key Features**:
  - CRUD operations for shows (podcasts, documentaries)
  - Multi-provider content import (YouTube, Vimeo, Mock)
  - Advanced filtering and search capabilities
  - Async import job management with progress tracking
  - Comprehensive validation and audit trails
  - Rate-limited external API integrations

### 2. CMS Discovery Service (`services/cms-discovery`)
**Read-optimized public discovery service**

- **Purpose**: Public-facing search and discovery API
- **Port**: 8079
- **Search Engine**: Elasticsearch
- **Key Features**:
  - Fast full-text search with fuzzy matching
  - Real-time content indexing via RabbitMQ events
  - Advanced filtering, sorting, and pagination
  - Public read-only API (no authentication required)
  - Search highlighting and suggestions
  - Category and tag-based browsing

## 🛠️ Technology Stack

### Core Technologies
- **Java 17** - Programming language
- **Spring Boot 3.4.5** - Application framework
- **Maven** - Build and dependency management
- **Docker & Docker Compose** - Containerization and local development

### Data & Messaging
- **PostgreSQL 17** - Primary database for CMS service
- **Elasticsearch 8.11** - Search engine for discovery service
- **RabbitMQ 3.12** - Event messaging between services
- **Kibana 8.11** - Elasticsearch visualization and monitoring

### Additional Libraries
- **Spring Data JPA** - Data access layer
- **Spring Data Elasticsearch** - Elasticsearch integration
- **Spring AMQP** - RabbitMQ messaging
- **Flyway** - Database migrations
- **MapStruct** - Entity-DTO mapping
- **Swagger/OpenAPI** - API documentation
- **Testcontainers** - Integration testing

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose

### 1. Clone and Setup
```bash
git clone <repository-url>
cd cms
```

### 2. Start Infrastructure Services
```bash
# Start PostgreSQL, Elasticsearch, Kibana, and RabbitMQ
docker-compose up -d

# Verify all services are healthy
docker-compose ps
```

### 3. Run CMS Service
```bash
cd services/cms
mvn spring-boot:run
```

### 4. Run Discovery Service
```bash
cd services/cms-discovery
mvn spring-boot:run
```

### 5. Verify Setup
- **CMS Service API**: http://localhost:8078/swagger-ui.html
- **Discovery Service API**: http://localhost:8079/swagger-ui.html
- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 📚 API Documentation

### CMS Service (Admin API)
- **Swagger UI**: http://localhost:8078/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8078/v3/api-docs
- **Health Check**: http://localhost:8078/actuator/health

### Discovery Service (Public API)
- **Swagger UI**: http://localhost:8079/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8079/v3/api-docs
- **Health Check**: http://localhost:8079/actuator/health

## 🔧 Development

### Building the Project
```bash
# Build all services
mvn clean install

# Build specific service
cd services/cms
mvn clean package
```

### Running Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd services/cms-discovery
mvn test
```

### Environment Configuration
Key environment variables for local development:

```bash
# CMS Service
export CMS_SERVICE_PORT=8078
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DATABASE=cms_db
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres

# Discovery Service
export SERVER_PORT=8079
export ELASTICSEARCH_URIS=http://localhost:9200
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672

# External Providers (Optional)
export YOUTUBE_API_KEY=your-youtube-api-key
export YOUTUBE_CHANNEL_ID=your-channel-id
export VIMEO_CLIENT_ID=your-vimeo-client-id
export VIMEO_CLIENT_SECRET=your-vimeo-client-secret
```

## 🚢 Release Process

To release services from this monorepo:

1. **Create a GitHub release** with a semantic version tag (e.g., `v1.2.3`)
2. **Specify components** in the release body using the following format:
   - For CMS service: `service:cms`
   - For Discovery service: `service:cms-discovery`
3. **Multiple services** can be released simultaneously
4. **CI workflow** will build and publish Docker images for specified services
5. **Components not mentioned** in the release body will be skipped

### Example Release Body:
```
Release v1.2.3

Includes:
- service:cms
- service:cms-discovery

Release notes:
- Added YouTube import functionality
- Improved search performance with new Elasticsearch mappings
- Fixed pagination issues in discovery API
```

## 🔍 Monitoring & Health Checks

### Service Health Endpoints
```bash
# CMS Service Health
curl http://localhost:8078/actuator/health

# Discovery Service Health
curl http://localhost:8079/actuator/health

# Detailed metrics
curl http://localhost:8078/actuator/metrics
curl http://localhost:8079/actuator/metrics
```

### Infrastructure Health
```bash
# Elasticsearch cluster health
curl http://localhost:9200/_cluster/health

# RabbitMQ management (requires authentication)
curl -u guest:guest http://localhost:15672/api/overview

# PostgreSQL connection test
docker exec cms-postgres pg_isready -U postgres -d cms_db
```

### Monitoring Dashboards
- **Kibana**: http://localhost:5601 - Elasticsearch data visualization
- **RabbitMQ Management**: http://localhost:15672 - Queue monitoring and management

## 🧪 Testing

### Integration Testing
The project uses Testcontainers for integration testing with real database and messaging infrastructure:

```bash
# Run integration tests
mvn verify

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

### API Testing
Postman collections are available in each service directory:
- `services/cms/postman/` - CMS Service API collection
- `services/cms-discovery/postman/` - Discovery Service API collection

## 🐛 Troubleshooting

### Common Issues

#### 1. Services Won't Start
```bash
# Check if ports are available
netstat -tulpn | grep -E ':(8078|8079|5432|9200|5672)'

# Restart infrastructure services
docker-compose down && docker-compose up -d
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Verify database exists
docker exec -it cms-postgres psql -U postgres -l
```

#### 3. Elasticsearch Connection Failed
```bash
# Check Elasticsearch status
curl http://localhost:9200/_cat/health

# View Elasticsearch logs
docker-compose logs elasticsearch
```

#### 4. RabbitMQ Message Processing Issues
```bash
# Check queue status
curl -u guest:guest http://localhost:15672/api/queues

# View RabbitMQ logs
docker-compose logs rabbitmq
```

#### 5. No Search Results in Discovery Service
```bash
# Check if events are being consumed
curl -u guest:guest http://localhost:15672/api/queues/%2F/cms-discovery.shows

# Verify Elasticsearch index
curl http://localhost:9200/cms-shows/_count

# Check if CMS service is publishing events
docker-compose logs rabbitmq | grep "cms.events"
```

### Log Locations
- **Application logs**: `services/*/logs/`
- **Docker logs**: `docker-compose logs <service-name>`
- **Spring Boot logs**: Console output when running with `mvn spring-boot:run`

## 🤝 Contributing

### Development Workflow
1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes** following the coding standards
4. **Add tests** for new functionality
5. **Ensure all tests pass**: `mvn test`
6. **Update documentation** if needed
7. **Submit a pull request**

### Code Standards
- Follow Java coding conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Maintain test coverage above 80%
- Use Spring Boot best practices

### Database Migrations
When adding database changes:
1. Create Flyway migration scripts in `services/cms/src/main/resources/db/migration/`
2. Follow naming convention: `V{version}__{description}.sql`
3. Test migrations on clean database
4. Update entity classes and repositories accordingly

## 📄 License

This project is licensed under the Apache 2.0 License - see the individual service README files for more details.

## 📞 Support

For questions and support:
1. Check the individual service README files for detailed documentation
2. Review the troubleshooting section above
3. Check existing GitHub issues
4. Create a new issue with detailed description and logs

---

## 📋 Service-Specific Documentation

For detailed information about each service, refer to their individual README files:

- **[CMS Service Documentation](services/cms/README.md)** - Comprehensive guide for the content management service
- **[Discovery Service Documentation](services/cms-discovery/README.md)** - Detailed documentation for the search and discovery service

These service-specific READMEs contain:
- Detailed API endpoint documentation
- Configuration options and environment variables
- Service-specific troubleshooting guides
- Development and deployment instructions
- External provider integration guides

