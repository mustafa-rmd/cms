# CMS Service Postman Collection

This Postman collection provides comprehensive API testing for the Thmanyah Content Management System (CMS) Service, including the new **Async Import Operations** with background job processing using RabbitMQ.

## Overview

The CMS Service manages shows (podcasts and documentaries) with full CRUD operations, filtering capabilities, user management, and both synchronous and asynchronous external content import functionality.

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

### 7. Import Operations (Synchronous)
- **Import from YouTube**: Bulk import from YouTube API with specified topic (blocking)
- **Import from Vimeo**: Bulk import from Vimeo API with specified topic (blocking)
- **Import from Mock Provider**: Test import functionality with specified topic (blocking)
- **Get Import Job Status**: Monitor import progress
- **Cancel Import Job**: Stop running imports
- **Get All Import Jobs**: List import history
- **Get Available Providers**: List supported providers
- **Check Provider Health**: Verify provider connectivity

### 8. **🚀 Async Import Operations (NEW)**
- **Start Async Import - YouTube**: Non-blocking YouTube import with RabbitMQ job queue
- **Start Async Import - Vimeo**: Non-blocking Vimeo import with RabbitMQ job queue
- **Start Async Import - Mock Provider**: Non-blocking mock import for testing
- **Get Import Job Status**: Real-time job status and progress tracking
- **Cancel Import Job**: Cancel queued or running async jobs
- **Retry Failed Import Job**: Retry failed jobs with exponential backoff
- **Get My Import Jobs**: View current user's import jobs
- **Get All Import Jobs (Admin)**: Admin view of all import jobs with filtering
- **Get Import Jobs by Status**: Filter jobs by status (QUEUED, PROCESSING, COMPLETED, etc.)
- **Get Import Statistics**: Comprehensive import metrics and analytics
- **Get Available Providers (Async)**: Provider info with rate limits and batch sizes

**Import Request Format:**
```json
{
  "topic": "education",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "skipDuplicates": true
}
```

## 🚀 Async Import Operations Features

### Key Benefits
- **Non-blocking**: Returns `202 Accepted` immediately with job ID
- **Background Processing**: Jobs processed by RabbitMQ workers
- **Real-time Progress**: Track job status and progress in real-time
- **Fault Tolerance**: Automatic retry with exponential backoff
- **Horizontal Scaling**: Add more worker instances for better performance
- **Job Management**: Cancel, retry, and monitor jobs

### Job Status Values
- `QUEUED`: Job queued for processing
- `PROCESSING`: Job is being processed
- `FETCHING`: Fetching data from external provider
- `SAVING`: Saving data to database
- `COMPLETED`: Job completed successfully
- `FAILED`: Job failed
- `CANCELLED`: Job was cancelled
- `RETRYING`: Job is being retried after failure

### Async vs Synchronous Import

| Feature | Synchronous | Async |
|---------|-------------|-------|
| Response Time | 30+ seconds | < 1 second |
| User Experience | Blocking | Non-blocking |
| Progress Tracking | None | Real-time |
| Fault Tolerance | None | Automatic retry |
| Scalability | Limited | Horizontal |
| Monitoring | Basic | Comprehensive |

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
- **`asyncJobId`**: Job ID from async import operations (auto-captured)
- **`jobStatus`**: Job status filter for queries (default: "PROCESSING")

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

## 🧪 Async Import Testing Workflow

### Quick Start with Async Imports
1. **Login**: Use "Login - Admin" to authenticate
2. **Start Async Import**: Run "Start Async Import - YouTube"
   - Returns `202 Accepted` immediately
   - Job ID automatically saved to `asyncJobId` variable
3. **Monitor Progress**: Run "Get Import Job Status" repeatedly
   - Uses the captured job ID
   - Watch status change: QUEUED → PROCESSING → FETCHING → SAVING → COMPLETED
4. **View Results**: Check "Get My Import Jobs" to see job history

### Advanced Testing
- **Cancel Jobs**: Test job cancellation with "Cancel Import Job"
- **Retry Failed Jobs**: Use "Retry Failed Import Job" for failed imports
- **Monitor System**: Use "Get Import Statistics" for system health
- **Filter Jobs**: Use "Get Import Jobs by Status" with different status values

### Test Scripts Features
The collection includes automated test scripts that:
- ✅ Validate HTTP status codes (202 for async start, 200 for status)
- ✅ Check response structure and required fields
- ✅ Auto-capture job IDs for subsequent requests
- ✅ Log progress information to Postman console
- ✅ Verify job status transitions and completion

## Notes

- All endpoints support pagination where applicable
- Filtering endpoints provide comprehensive search capabilities
- **NEW**: Async import operations provide non-blocking background processing
- Import operations support both synchronous and asynchronous modes
- The service includes comprehensive validation and error handling
- RabbitMQ is required for async import functionality
