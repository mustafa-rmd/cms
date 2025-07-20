# CMS Discovery Service - Postman Collection

This Postman collection provides comprehensive testing capabilities for the CMS Discovery Service API. The service offers public, read-only access to content search and discovery features powered by Elasticsearch.

## 📋 Collection Overview

### **Collection Structure**

1. **🔍 Search Operations** - Core search functionality
2. **📊 Content Discovery** - Popular, recent, and specific content retrieval
3. **🏷️ Content Filtering** - Filter by type, tags, and categories
4. **🧪 Search Examples & Edge Cases** - Advanced search scenarios and validation
5. **📖 API Documentation** - Swagger UI and OpenAPI access
6. **🏥 Health & Monitoring** - Service health and metrics

## 🚀 Quick Start

### **1. Import Collection**
- Download `CMS_Discovery_Service.postman_collection.json`
- Import into Postman: File → Import → Select file

### **2. Set Environment Variables**
The collection uses these variables (automatically configured):
- `baseUrl`: http://localhost:8079
- `showId`: 123e4567-e89b-12d3-a456-426614174000
- `tag`: technology
- `category`: Science

### **3. Start Dependencies**
```bash
cd services/cms-discovery
docker-compose up -d
```

### **4. Run Discovery Service**
```bash
# From services directory
mvn spring-boot:run -pl cms-discovery
```

## 🔍 Search Operations

### **Basic Text Search**
Simple keyword search with pagination:
```
GET /api/v1/search?query=artificial intelligence&page=0&size=10
```

### **Advanced Search with Filters**
Complex search with multiple filters:
```
GET /api/v1/search?query=technology&type=podcast&language=en&minDuration=300&maxDuration=3600&sortBy=rating&sortDirection=desc
```

### **Search with Tags and Categories**
Filter by content metadata:
```
GET /api/v1/search?query=machine learning&tags=ai,technology&categories=Science,Technology&provider=youtube
```

### **Search with Date Range**
Time-based filtering:
```
GET /api/v1/search?query=documentary&publishedAfter=2024-01-01&publishedBefore=2024-12-31&minRating=4.0
```

### **Advanced Search (POST)**
Complex search using request body:
```json
POST /api/v1/search
{
  "query": "artificial intelligence",
  "type": "podcast",
  "language": "en",
  "tags": ["technology", "ai", "future"],
  "categories": ["Science", "Technology"],
  "minDuration": 600,
  "maxDuration": 7200,
  "publishedAfter": "2024-01-01",
  "minRating": 3.5,
  "sortBy": "rating",
  "sortDirection": "desc",
  "page": 0,
  "size": 25,
  "highlight": true,
  "fuzzy": true
}
```

## 📊 Content Discovery

### **Popular Content**
Get most viewed content:
```
GET /api/v1/shows/popular?page=0&size=10
```

### **Recent Content**
Get recently created content:
```
GET /api/v1/shows/recent?page=0&size=15
```

### **Specific Content**
Get content by ID:
```
GET /api/v1/shows/{showId}
```

## 🏷️ Content Filtering

### **By Content Type**
- Podcasts: `GET /api/v1/shows/type/podcast`
- Documentaries: `GET /api/v1/shows/type/documentary`

### **By Tags**
```
GET /api/v1/shows/tag/{tag}?page=0&size=10
```

### **By Categories**
```
GET /api/v1/shows/category/{category}?page=0&size=10
```

## 🧪 Search Examples & Edge Cases

### **Fuzzy Search (Typo Tolerance)**
Test search with intentional typos:
```
GET /api/v1/search?query=artifical inteligence&fuzzy=true&highlight=true
```

### **Large Page Sizes**
Test maximum page size (100):
```
GET /api/v1/search?query=technology&size=100&page=0
```

### **Invalid Parameters**
Test validation with invalid page size:
```
GET /api/v1/search?query=test&size=150&page=0
```
*Expected: 400 Bad Request*

### **Complex Filter Combinations**
Test multiple filters together:
```json
POST /api/v1/search
{
  "query": "science",
  "type": "documentary",
  "language": "en",
  "provider": "youtube",
  "tags": ["science", "education", "research"],
  "categories": ["Science", "Education"],
  "minDuration": 1800,
  "maxDuration": 5400,
  "publishedAfter": "2023-01-01",
  "publishedBefore": "2024-12-31",
  "minRating": 4.0,
  "sortBy": "rating",
  "sortDirection": "desc"
}
```

## 📖 API Documentation

### **Swagger UI**
Interactive API documentation:
```
GET /swagger-ui.html
```

### **OpenAPI Specification**
Raw API specification:
```
GET /v3/api-docs
```

## 🏥 Health & Monitoring

### **Health Check**
Service health status:
```
GET /actuator/health
```

### **Service Information**
Service metadata:
```
GET /actuator/info
```

### **Metrics**
Service metrics:
```
GET /actuator/metrics
```

## 🎯 Search Parameters Reference

### **Query Parameters**

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `query` | string | Search text | `artificial intelligence` |
| `type` | string | Content type | `podcast`, `documentary` |
| `language` | string | Language code | `en`, `es`, `fr` |
| `provider` | string | Content provider | `youtube`, `vimeo`, `internal` |
| `tags` | string | Comma-separated tags | `technology,ai,future` |
| `categories` | string | Comma-separated categories | `Science,Technology` |
| `minDuration` | integer | Min duration (seconds) | `300` |
| `maxDuration` | integer | Max duration (seconds) | `3600` |
| `publishedAfter` | date | Published after | `2024-01-01` |
| `publishedBefore` | date | Published before | `2024-12-31` |
| `minRating` | number | Minimum rating | `4.0` |
| `sortBy` | string | Sort field | `relevance`, `createdDate`, `rating`, `viewCount` |
| `sortDirection` | string | Sort direction | `asc`, `desc` |
| `page` | integer | Page number (0-based) | `0` |
| `size` | integer | Page size (1-100) | `20` |
| `highlight` | boolean | Enable highlighting | `true`, `false` |
| `fuzzy` | boolean | Enable fuzzy search | `true`, `false` |

## 🔧 Testing Scenarios

### **Functional Testing**
1. **Basic Search**: Test simple text queries
2. **Filtering**: Test all filter combinations
3. **Sorting**: Test different sort fields and directions
4. **Pagination**: Test page navigation
5. **Content Discovery**: Test popular/recent endpoints

### **Edge Case Testing**
1. **Empty Queries**: Test browsing without search terms
2. **Invalid Parameters**: Test validation errors
3. **Large Datasets**: Test performance with large page sizes
4. **Fuzzy Search**: Test typo tolerance
5. **Complex Filters**: Test multiple filter combinations

### **Performance Testing**
1. **Response Times**: Monitor search execution times
2. **Large Results**: Test with high result counts
3. **Complex Queries**: Test advanced search performance

## 🚨 Expected Responses

### **Success Responses**
- **200 OK**: Successful search with results
- **200 OK**: Empty results (valid search, no matches)

### **Error Responses**
- **400 Bad Request**: Invalid parameters (size > 100, negative page)
- **404 Not Found**: Show ID not found
- **500 Internal Server Error**: Search service errors

## 📝 Notes

- **No Authentication Required**: All endpoints are public
- **Read-Only**: No data modification operations
- **Rate Limiting**: May be implemented in production
- **CORS**: Configured for web client access
- **Caching**: Responses may be cached for performance

## 🔗 Related Services

- **CMS Service**: Content creation and management
- **Elasticsearch**: Search engine backend
- **RabbitMQ**: Event processing for real-time indexing

## 📞 Support

For issues or questions:
- Check service logs: `docker-compose logs cms-discovery`
- Verify dependencies: `docker-compose ps`
- Review API documentation: `/swagger-ui.html`
