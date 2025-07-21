# Content Management System (CMS) Service

This application is used to manage shows and content for the CMS platform. It provides CRUD operations for shows including podcasts and documentaries.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Tech Stack](#tech-stack)
- [Setup](#setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Endpoints](#endpoints)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

List of tools and versions required to run the project:

- Java (version 17 or higher)
- Maven (version 3.x or higher)
- Docker
- PostgreSQL

## Tech Stack

- Spring Boot 3.4.5 for application development
- Spring Data JPA for data access
- Hibernate as the ORM
- PostgreSQL 17 for the database
- Flyway for database migrations
- MapStruct for entity-DTO mapping
- Swagger/OpenAPI for API documentation
- Docker for containerization

## Setup

1. Clone the repository
2. Navigate to the CMS service directory: `cd services/cms`
3. Install dependencies: `mvn clean install`
4. Set up PostgreSQL database
5. Configure environment variables (see Configuration section)

## Configuration

### Environment Variables

#### Service Configuration
- `CMS_SERVICE_PORT`: Port for the CMS service (default: 8078)
- `CMS_SERVICE_NAME`: Service name (default: cms)
- `POSTGRES_HOST`: PostgreSQL host (default: localhost)
- `POSTGRES_PORT`: PostgreSQL port (default: 5432)
- `POSTGRES_DATABASE`: Database name (default: thmanyah)
- `POSTGRES_USER`: Database username (default: postgres)
- `POSTGRES_PASSWORD`: Database password (default: postgres)
- `CMS_SCHEMA`: Database schema (default: public)
- `FLYWAY_MIGRATION_ENABLED`: Enable Flyway migrations (default: true)
- `JPA_SHOW_SQL`: Show SQL queries in logs (default: true)
- `JPA_DDL_AUTO`: Hibernate DDL auto mode (default: update)

#### External Provider Configuration
- `YOUTUBE_API_KEY`: YouTube Data API v3 key (required for YouTube imports)
- `YOUTUBE_CHANNEL_ID`: YouTube channel ID to import from (required for YouTube imports)
- `VIMEO_CLIENT_ID`: Vimeo OAuth2 client ID (configured)
- `VIMEO_CLIENT_SECRET`: Vimeo OAuth2 client secret (configured)
- `MOCK_PROVIDER_ENABLED`: Enable mock provider for testing (default: true)

## Running the Application

### Local Development

```bash
mvn spring-boot:run
```

### Docker

```bash
docker build -t cms-service .
docker run -p 8078:8078 cms-service
```

### With Docker Compose

```bash
docker-compose up
```

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8078/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8078/v3/api-docs

## Database Schema

The CMS service uses a PostgreSQL database with the following main table:

### Shows Table

- `id` (UUID): Primary key
- `type` (VARCHAR): Show type (podcast, documentary)
- `title` (VARCHAR): Show title (unique)
- `description` (TEXT): Show description
- `language` (VARCHAR): ISO 639-1 language code
- `duration_sec` (INTEGER): Duration in seconds
- `published_at` (DATE): Publication date
- `created_date` (TIMESTAMP): Creation timestamp
- `updated_date` (TIMESTAMP): Last update timestamp
- `created_by` (VARCHAR): Creator email
- `updated_by` (VARCHAR): Last updater email

### Indexes

The table is optimized for write-heavy operations with the following indexes:
- Primary key on `id`
- Indexes on `type`, `language`, `published_at`, `created_date`, `title`
- Unique index on `LOWER(title)` for case-insensitive uniqueness
- Composite indexes for common query patterns
- Index on `created_by` for user-specific queries

## Endpoints

### Show Management

- `GET /api/v1/shows` - Get all shows with pagination
- `GET /api/v1/shows/{id}` - Get show by ID
- `POST /api/v1/shows` - Create a new show
- `PUT /api/v1/shows/{id}` - Update a show
- `DELETE /api/v1/shows/{id}` - Delete a show
- `DELETE /api/v1/shows` - Delete multiple shows

### Filtering and Search

- `GET /api/v1/shows/type/{type}` - Get shows by type
- `GET /api/v1/shows/language/{language}` - Get shows by language
- `GET /api/v1/shows/search?title={title}` - Search shows by title
- `GET /api/v1/shows/published/{publishedAt}` - Get shows by published date
- `GET /api/v1/shows/creator/{userEmail}` - Get shows by creator
- `POST /api/v1/shows/advanced-filters` - Advanced filtering with custom criteria

### Metadata

- `GET /api/v1/shows/filtering-values` - Get distinct field values for filtering
- `GET /api/v1/shows/statistics` - Get show statistics

### Import Operations (Async Only)

**Note**: All import operations are now asynchronous. The synchronous import endpoints have been removed.

- `POST /api/v1/import/async/{provider}` - Start async import job from external provider
- `GET /api/v1/import/async/jobs/{jobId}` - Get import job status and progress
- `POST /api/v1/import/async/jobs/{jobId}/cancel` - Cancel import job
- `POST /api/v1/import/async/jobs/{jobId}/retry` - Retry failed import job
- `GET /api/v1/import/async/jobs/my` - Get current user's import jobs
- `GET /api/v1/import/async/jobs` - Get all import jobs (admin only)
- `GET /api/v1/import/async/jobs?status={status}` - Get import jobs by status
- `GET /api/v1/import/async/stats` - Get import statistics
- `GET /api/v1/import/async/providers` - Get available providers

### Health and Monitoring

- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Application metrics

## Features

### Core Features
- **Write-Heavy Optimization**: Database schema and indexes optimized for high write throughput
- **Advanced Filtering**: Support for complex filtering using the advanced filters library
- **Validation**: Comprehensive input validation with meaningful error messages
- **Pagination**: All list endpoints support pagination
- **Search**: Full-text search capabilities for show titles
- **Statistics**: Built-in statistics and analytics endpoints
- **Audit Trail**: Tracks creation and modification metadata
- **Type Safety**: Strong typing with enums and validation constraints

### Import Features
- **Multi-Provider Support**: Import from YouTube, Vimeo, and mock providers
- **Async Processing**: Non-blocking import operations with progress tracking
- **Rate Limiting**: Respects external API rate limits and quotas
- **Duplicate Handling**: Configurable duplicate detection and skipping
- **Job Management**: Track, monitor, and cancel import jobs
- **Provider Health Checks**: Monitor external provider availability
- **Batch Processing**: Configurable batch sizes for optimal performance

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for your changes
5. Ensure all tests pass
6. Submit a pull request

## External Providers

The CMS service supports importing content from multiple external providers:

### YouTube Provider
- **Requirements**: YouTube Data API v3 key and channel ID
- **Features**: Real-time import from YouTube channels
- **Configuration**: Set `YOUTUBE_API_KEY` and `YOUTUBE_CHANNEL_ID` environment variables
- **Rate Limits**: Respects YouTube API quotas (100 requests/minute)
- **Content Types**: Automatically categorizes videos as podcasts or documentaries

### Vimeo Provider
- **Requirements**: Vimeo OAuth2 credentials (configured)
- **Features**: Real-time import from public Vimeo videos
- **Authentication**: Dynamic OAuth2 token generation per request
- **Rate Limits**: 60 requests/minute
- **Content Types**: Supports both podcasts and documentaries
- **Endpoint**: Uses `/videos` search endpoint for public content

### Mock Provider
- **Purpose**: Testing and development
- **Features**: Generates realistic mock data
- **Configuration**: Enabled by default, disable with `MOCK_PROVIDER_ENABLED=false`
- **Content**: Includes English and Arabic content examples

### Getting YouTube API Key (Free)
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable YouTube Data API v3
4. Create credentials (API Key)
5. Set the API key in `YOUTUBE_API_KEY` environment variable
6. Find your channel ID and set it in `YOUTUBE_CHANNEL_ID`

**Note**: YouTube API provides 10,000 quota units per day for free, which is sufficient for most use cases.

### Getting Vimeo OAuth2 Credentials
1. Create a Vimeo app at [Vimeo Developer Portal](https://developer.vimeo.com/)
2. Get your Client ID and Client Secret
3. Set the credentials in environment variables:
   ```bash
   VIMEO_CLIENT_ID=your-client-id
   VIMEO_CLIENT_SECRET=your-client-secret
   ```

**Note**: The application automatically generates fresh access tokens for each request using the OAuth2 client credentials flow. No manual token management required!

**Provided credentials are already configured** - the system will automatically:
- Generate fresh access tokens for each API call
- Handle token expiration automatically
- Use the `/oauth/authorize/client` endpoint for authentication
- Call `/me/videos` with the generated Bearer token

## License

This project is licensed under the Apache 2.0 License.
