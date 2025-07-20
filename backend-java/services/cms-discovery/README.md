# CMS Discovery Service

A read-only, public-facing service optimized for content search and discovery. This service consumes events from the CMS service via RabbitMQ and indexes content in Elasticsearch for fast, scalable search operations.

## Architecture

```
[CMS Service]
  └─ Save/Import Show
       └─ Publish ShowCreatedEvent → 🐰 RabbitMQ
            ↓
[Discovery Service]
  └─ Consume ShowCreatedEvent
       └─ Save to Elasticsearch index
            ↓
  └─ API: GET /shows/search?q=...
```

## Features

- **Public Read-Only API**: No authentication required for search operations
- **Elasticsearch Integration**: Fast full-text search with advanced filtering
- **Event-Driven Architecture**: Real-time content indexing via RabbitMQ
- **Advanced Search**: Support for fuzzy search, highlighting, sorting, and pagination
- **RESTful API**: Clean, well-documented REST endpoints
- **Swagger Documentation**: Interactive API documentation
- **Health Monitoring**: Actuator endpoints for monitoring

## Technology Stack

- **Java 17** with Spring Boot 3.4.5
- **Elasticsearch** for search and indexing
- **RabbitMQ** for event consumption
- **Spring Data Elasticsearch** for repository layer
- **Spring AMQP** for message handling
- **Swagger/OpenAPI** for documentation

## Quick Start

### Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven 3.6+

### 1. Start Dependencies

```bash
# Start Elasticsearch, Kibana, RabbitMQ, and PostgreSQL
docker-compose up -d

# Wait for services to be healthy
docker-compose ps
```

### 2. Run the Service

```bash
# Build and run
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/cms-discovery-0.0.1.jar
```

### 3. Verify Setup

- **Discovery Service**: http://localhost:8079
- **Swagger UI**: http://localhost:8079/swagger-ui.html
- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **PostgreSQL**: localhost:5432 (cms_user/cms_password/cms_db)

## API Endpoints

### Search Operations

- `GET /api/v1/search` - Search shows with query parameters
- `POST /api/v1/search` - Advanced search with request body
- `GET /api/v1/shows/{id}` - Get show by ID
- `GET /api/v1/shows/popular` - Get popular shows
- `GET /api/v1/shows/recent` - Get recent shows
- `GET /api/v1/shows/type/{type}` - Get shows by type
- `GET /api/v1/shows/tag/{tag}` - Get shows by tag
- `GET /api/v1/shows/category/{category}` - Get shows by category

### Health & Monitoring

- `GET /actuator/health` - Service health status
- `GET /actuator/info` - Service information
- `GET /actuator/metrics` - Service metrics

## Search Examples

### Basic Text Search
```bash
curl "http://localhost:8079/api/v1/search?query=artificial%20intelligence"
```

### Filtered Search
```bash
curl "http://localhost:8079/api/v1/search?query=technology&type=podcast&language=en&page=0&size=10"
```

### Advanced Search
```bash
curl -X POST "http://localhost:8079/api/v1/search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "machine learning",
    "type": "documentary",
    "tags": ["technology", "ai"],
    "minDuration": 1800,
    "sortBy": "rating",
    "sortDirection": "desc",
    "page": 0,
    "size": 20
  }'
```

### Browse by Category
```bash
curl "http://localhost:8079/api/v1/shows/category/Technology?page=0&size=10"
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8079 | Service port |
| `ELASTICSEARCH_URIS` | http://localhost:9200 | Elasticsearch connection |
| `RABBITMQ_HOST` | localhost | RabbitMQ host |
| `RABBITMQ_PORT` | 5672 | RabbitMQ port |
| `POSTGRES_HOST` | localhost | PostgreSQL host (for CMS service) |
| `POSTGRES_PORT` | 5432 | PostgreSQL port (for CMS service) |
| `POSTGRES_DB` | cms_db | PostgreSQL database name |
| `POSTGRES_USER` | cms_user | PostgreSQL username |
| `POSTGRES_PASSWORD` | cms_password | PostgreSQL password |
| `ELASTICSEARCH_INDEX_NAME` | cms-shows | Elasticsearch index name |
| `SEARCH_DEFAULT_PAGE_SIZE` | 20 | Default search page size |
| `SEARCH_MAX_PAGE_SIZE` | 100 | Maximum search page size |

### Application Properties

Key configuration in `application.yaml`:

```yaml
cms-discovery:
  elasticsearch:
    index-name: cms-shows
    refresh-policy: wait_for
  rabbitmq:
    exchange: cms.events
    queue: cms-discovery.shows
    routing-key: show.created
  search:
    default-page-size: 20
    max-page-size: 100
    highlight-enabled: true
    fuzzy-enabled: true
```

## CMS Service Integration

This docker-compose setup includes a PostgreSQL database that can be used by the CMS service. To connect the CMS service to this database, update the CMS service's `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cms_db
    username: cms_user
    password: cms_password
    driver-class-name: org.postgresql.Driver
```

Or set environment variables:
```bash
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=cms_db
export POSTGRES_USER=cms_user
export POSTGRES_PASSWORD=cms_password
```

## Event Processing

The service listens for `ShowCreatedEvent` messages from the CMS service:

```json
{
  "showId": "123e4567-e89b-12d3-a456-426614174000",
  "title": "The Future of AI",
  "description": "An in-depth look at artificial intelligence...",
  "type": "podcast",
  "language": "en",
  "durationSec": 3600,
  "publishedAt": "2024-01-15",
  "provider": "youtube",
  "tags": ["technology", "ai", "future"],
  "categories": ["Science", "Technology"],
  "isPublic": true,
  "isActive": true,
  "eventTimestamp": "2024-01-15T10:30:00",
  "eventType": "SHOW_CREATED"
}
```

## Development

### Project Structure

```
src/main/java/com/thmanyah/services/cmsdiscovery/
├── config/              # Configuration classes
├── controllers/         # REST controllers
├── exception/           # Exception handling
├── model/              # Data models and DTOs
│   ├── dto/            # Data Transfer Objects
│   └── event/          # Event models
├── repository/         # Elasticsearch repositories
└── services/           # Business logic services
```

### Adding New Search Features

1. **Add search method to `ShowRepository`**
2. **Implement business logic in `SearchService`**
3. **Add endpoint to `SearchController`**
4. **Update API documentation**

## Monitoring

### Elasticsearch Health

```bash
curl http://localhost:9200/_cluster/health
```

### RabbitMQ Queue Status

```bash
curl -u guest:guest http://localhost:15672/api/queues/%2F/cms-discovery.shows
```

### PostgreSQL Connection

```bash
# Connect to PostgreSQL (for CMS service database)
psql -h localhost -p 5432 -U cms_user -d cms_db

# Check tables
\dt
```

### Service Health

```bash
curl http://localhost:8079/actuator/health
```

## Troubleshooting

### Common Issues

1. **Elasticsearch connection failed**
   - Verify Elasticsearch is running: `docker-compose ps`
   - Check logs: `docker-compose logs elasticsearch`

2. **RabbitMQ connection failed**
   - Verify RabbitMQ is running: `docker-compose ps`
   - Check credentials in configuration

3. **PostgreSQL connection failed (for CMS service)**
   - Verify PostgreSQL is running: `docker-compose ps`
   - Check connection details: host=localhost, port=5432, db=cms_db, user=cms_user

4. **No search results**
   - Verify events are being consumed: Check RabbitMQ management UI
   - Check Elasticsearch index: `curl http://localhost:9200/cms-shows/_count`
   - Verify CMS service is publishing events to RabbitMQ

### Logs

```bash
# Service logs
tail -f logs/cms-discovery.log

# Docker logs
docker-compose logs -f cms-discovery-elasticsearch
docker-compose logs -f cms-discovery-rabbitmq
docker-compose logs -f cms-postgres
```

## Production Deployment

### Environment-Specific Configuration

- Set appropriate Elasticsearch cluster URLs
- Configure RabbitMQ cluster connection
- Set proper logging levels
- Configure monitoring and alerting
- Set up index lifecycle management

### Security Considerations

- Enable Elasticsearch security in production
- Use proper RabbitMQ authentication
- Configure CORS for web clients
- Set up rate limiting
- Monitor for abuse and implement throttling
