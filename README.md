# Thmanyah Platform

A comprehensive platform for content management and discovery, built with modern microservices architecture.

## 📁 Repository Structure

```
├── backend-java/          # Java-based backend services
│   ├── services/
│   │   ├── cms/           # Content Management Service
│   │   └── cms-discovery/ # Content Discovery Service
│   ├── docker-compose.yml # Infrastructure services
│   ├── pom.xml           # Maven parent POM
│   └── README.md         # Backend documentation
└── README.md             # This file
```

## 🚀 Quick Start

### Backend Services (Java)

The backend consists of two microservices built with Java 17 and Spring Boot:

1. **Navigate to backend directory:**
   ```bash
   cd backend-java
   ```

2. **Start infrastructure services:**
   ```bash
   docker-compose up -d
   ```

3. **Run the services:**
   ```bash
   # CMS Service (port 8078)
   cd services/cms
   mvn spring-boot:run

   # Discovery Service (port 8079) - in another terminal
   cd services/cms-discovery
   mvn spring-boot:run
   ```

4. **Access the APIs:**
   - **CMS Service**: http://localhost:8078/swagger-ui.html
   - **Discovery Service**: http://localhost:8079/swagger-ui.html

## 📚 Documentation

- **[Backend Java Documentation](backend-java/README.md)** - Comprehensive guide for Java services
- **[CMS Service](backend-java/services/cms/README.md)** - Content management service details
- **[Discovery Service](backend-java/services/cms-discovery/README.md)** - Search and discovery service details

## 🏗️ Architecture

The platform uses an event-driven microservices architecture:

- **CMS Service**: Handles content CRUD operations and external provider imports
- **Discovery Service**: Provides fast search and discovery capabilities
- **Event Bus**: RabbitMQ for real-time communication between services
- **Data Stores**: PostgreSQL for CMS, Elasticsearch for search

## 🛠️ Technology Stack

### Backend
- **Java 17** with Spring Boot 3.4.5
- **PostgreSQL** for primary data storage
- **Elasticsearch** for search and indexing
- **RabbitMQ** for event messaging
- **Docker** for containerization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes in the appropriate service directory
4. Follow the coding standards and add tests
5. Submit a pull request

## 📄 License

This project is licensed under the Apache 2.0 License.
