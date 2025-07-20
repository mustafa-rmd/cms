# CMS Service Postman Collection

This Postman collection provides comprehensive API testing for the Thmanyah Content Management System (CMS) Service.

## Overview

The CMS Service manages shows (podcasts and documentaries) with full CRUD operations, filtering capabilities, user management, and external content import functionality.

## Collection Structure

### 1. Authentication
- **Login - Admin**: Authenticate as admin user
- **Login - Editor**: Authenticate as editor user  
- **Refresh Token**: Refresh access tokens
- **Logout**: End user session
- **Login - Invalid Credentials**: Test error handling

### 2. Show Management
- **Create Show**: Create new shows (ADMIN/EDITOR)
- **Get All Shows**: Retrieve paginated show list
- **Get Show by ID**: Retrieve specific show
- **Update Show**: Modify existing shows (ADMIN/EDITOR)
- **Delete Show**: Remove shows (ADMIN only)
- **Delete Multiple Shows**: Bulk delete (ADMIN only)

### 3. User Management (ADMIN Only)
- **Create User**: Add new users with roles
- **Get All Users**: List all users with pagination
- **Get Users by Role**: Filter users by role (ADMIN/EDITOR)
- **Get Active Users**: List only active users
- **Update User**: Modify user details
- **Delete User**: Deactivate users

### 4. Role-Based Access Examples
- Demonstrates different access levels for ADMIN vs EDITOR roles
- Shows successful operations and expected failures

### 5. Filtering and Search
- **Get Shows by Type**: Filter by podcast/documentary
- **Get Shows by Language**: Filter by language code
- **Get Shows by Provider**: Filter by content provider (internal/vimeo/youtube)
- **Search Shows by Title**: Case-insensitive title search
- **Get Shows by Published Date**: Filter by publication date
- **Get Shows by Creator**: Filter by creator email

### 6. Metadata and Statistics
- **Get Distinct Fields**: Retrieve unique values for filtering
- **Get Show Statistics**: Get aggregated statistics

### 7. Import Operations
- **Import from YouTube**: Bulk import from YouTube API with specified topic
- **Import from Vimeo**: Bulk import from Vimeo API with specified topic
- **Import from Mock Provider**: Test import functionality with specified topic
- **Get Import Job Status**: Monitor import progress
- **Cancel Import Job**: Stop running imports
- **Get All Import Jobs**: List import history
- **Get Available Providers**: List supported providers
- **Check Provider Health**: Verify provider connectivity

**Import Request Format:**
```json
{
  "topic": "education",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "skipDuplicates": true,
  "batchSize": 10
}
```

**Required Fields:**
- `topic`: Search topic for content import (e.g., "education", "technology", "science")
- `startDate`: Start date for content filtering (YYYY-MM-DD format)
- `endDate`: End date for content filtering (YYYY-MM-DD format)

**Optional Fields:**
- `skipDuplicates`: Skip duplicate content (default: true)
- `batchSize`: Number of items to process per batch (default: 10, max: 100)

### 8. Health and Monitoring
- **Health Check**: Application health status
- **Application Info**: Service information
- **Swagger UI**: Interactive API documentation
- **OpenAPI Docs**: API specification

## Variables

The collection uses the following variables:

- `baseUrl`: Service base URL (default: http://localhost:8078)
- `adminEmail`: Admin user email (admin@thmanyah.io)
- `editorEmail`: Editor user email
- `userEmail`: Regular user email
- `showId`: Sample show ID for testing
- `userId`: Sample user ID for testing
- `jobId`: Sample import job ID
- `adminAccessToken`: Admin JWT token (auto-populated)
- `adminRefreshToken`: Admin refresh token (auto-populated)
- `editorAccessToken`: Editor JWT token (auto-populated)
- `editorRefreshToken`: Editor refresh token (auto-populated)
- `importTopic`: Default topic for import operations (default: "education")

## Getting Started

1. **Import Collection**: Import the `CMS_Service.postman_collection.json` file
2. **Set Environment**: Configure the `baseUrl` variable for your environment
3. **Authenticate**: Run "Login - Admin" or "Login - Editor" to get tokens
4. **Test Endpoints**: Tokens are automatically saved and used in subsequent requests

## Authentication Flow

1. Login with credentials to get access and refresh tokens
2. Tokens are automatically stored in collection variables
3. Subsequent requests use the Bearer token authentication
4. Use refresh token endpoint when access token expires

## Role-Based Access

- **ADMIN**: Full access to all operations including user management and imports
- **EDITOR**: Can create, read, and update shows but cannot delete or manage users
- **Public**: Read-only access to show data (no authentication required)

## Error Testing

The collection includes requests that demonstrate error scenarios:
- Invalid credentials (401)
- Validation errors (400)
- Duplicate resources (409)
- Not found errors (404)
- Insufficient permissions (403)

## Notes

- All endpoints support pagination where applicable
- Filtering endpoints provide comprehensive search capabilities
- Import operations are asynchronous with job tracking
- The service includes comprehensive validation and error handling
