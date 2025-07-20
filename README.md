# Content Management System

A comprehensive platform for content management and discovery, featuring a modern Angular frontend with beautiful animations and a robust Java microservices backend. Built with cutting-edge technologies and inspired by Thmanyah.com's design aesthetic.

## 🎨 **Latest Updates - Animation System & Styling Overhaul**

### **✨ Frontend Enhancements (v2.0.0)**

#### **Comprehensive Animation System**
- **Page Transitions**: Smooth fade-in and slide effects
- **Interactive Hover Effects**: Cards lift, scale, and shimmer on hover
- **Staggered Animations**: Content cards animate in sequence
- **Floating Icons**: Gentle floating motion for visual elements
- **Loading States**: Enhanced spinner animations with pulse effects
- **Form Animations**: Bounce-in effects for login and search forms

#### **Modern Dark Theme Design**
- **Thmanyah.com Inspired**: Professional dark theme with elegant gradients
- **Arabic Content Support**: Full RTL support with Cairo font family
- **Responsive Design**: Mobile-first approach with smooth animations
- **Professional Color Palette**: Dark theme with accent colors
- **Enhanced Typography**: Modern font hierarchy and spacing

#### **Enhanced User Experience**
- **Smooth Interactions**: Every element has engaging hover effects
- **Visual Feedback**: Immediate response to user actions
- **Accessibility**: Reduced motion support and proper contrast
- **Performance Optimized**: 60fps animations with hardware acceleration

## 📁 **Repository Structure**

```
├── cms-frontend/                    # Angular 17 Frontend Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── auth/
│   │   │   │   │   └── login/       # Login component with animations
│   │   │   │   ├── home/            # Home page with staggered animations
│   │   │   │   ├── search/          # Search interface with smooth transitions
│   │   │   │   ├── shows/           # Show management
│   │   │   │   ├── users/           # User management
│   │   │   │   └── import/          # Import functionality
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts  # Authentication service
│   │   │   │   ├── search.service.ts # Search API integration
│   │   │   │   ├── show.service.ts  # Show management
│   │   │   │   └── import.service.ts # Import operations
│   │   │   ├── models/              # TypeScript interfaces
│   │   │   ├── interceptors/        # HTTP interceptors
│   │   │   └── app.component.ts     # Main app component
│   │   ├── styles.scss              # Global styles with animations
│   │   └── index.html               # Main HTML file
│   ├── package.json                 # Frontend dependencies
│   └── angular.json                 # Angular configuration
├── backend-java/                    # Java Microservices Backend
│   ├── services/
│   │   ├── cms/                     # Content Management Service
│   │   │   ├── src/main/java/
│   │   │   │   └── com/thmanyah/services/cms/
│   │   │   │       ├── controllers/ # REST API controllers
│   │   │   │       ├── services/    # Business logic services
│   │   │   │       ├── repositories/ # Data access layer
│   │   │   │       ├── model/       # Entity and DTO classes
│   │   │   │       └── config/      # Configuration classes
│   │   │   ├── pom.xml              # Maven configuration
│   │   │   └── README.md            # Service documentation
│   │   └── cms-discovery/           # Content Discovery Service
│   │       ├── src/main/java/
│   │       │   └── com/thmanyah/services/cmsdiscovery/
│   │       │       ├── controllers/ # Search API controllers
│   │       │       ├── services/    # Search and indexing services
│   │       │       ├── repository/  # Elasticsearch integration
│   │       │       └── model/       # Search models
│   │       ├── pom.xml              # Maven configuration
│   │       └── README.md            # Service documentation
│   ├── docker-compose.yml           # Infrastructure services
│   ├── pom.xml                      # Maven parent POM
│   └── README.md                    # Backend documentation
└── README.md                        # This file
```

## 🚀 **Platform Features**

### **Frontend (Angular 17)**
- **Modern UI/UX**: Responsive design with dark theme
- **Authentication**: JWT-based auth with role-based access
- **Content Management**: CRUD operations for shows and podcasts
- **Advanced Search**: Real-time search with filters and pagination
- **Import System**: Import from YouTube, Vimeo, and other providers
- **User Management**: Admin panel for user administration
- **Smooth Animations**: Engaging user interactions and transitions

### **Backend (Java Microservices)**
- **CMS Service**: Content management and external provider integration
- **Discovery Service**: Fast search and content discovery
- **Event-Driven Architecture**: RabbitMQ for service communication
- **Data Persistence**: PostgreSQL for CMS, Elasticsearch for search
- **Security**: JWT authentication and authorization
- **API Documentation**: Swagger/OpenAPI integration

## 🛠 **Technology Stack**

### **Frontend Stack**
- **Angular 17**: Latest version with standalone components
- **Bootstrap 5**: Responsive UI framework
- **Bootstrap Icons**: Beautiful icon library
- **RxJS**: Reactive programming
- **TypeScript**: Type-safe development
- **SCSS**: Advanced styling with animations

### **Backend Stack**
- **Java 17**: Latest LTS version
- **Spring Boot 3.4.5**: Modern Spring framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **PostgreSQL**: Primary database
- **Elasticsearch**: Search and indexing
- **RabbitMQ**: Message broker
- **Docker**: Containerization

### **Infrastructure**
- **Docker Compose**: Local development environment
- **Maven**: Build and dependency management
- **Swagger**: API documentation
- **JUnit**: Unit testing
- **Postman**: API testing collections

## 🎯 **Animation System (Frontend)**

### **Animation Types**
- **Entrance Animations**: `fade-in`, `fade-in-up`, `bounce-in`, `zoom-in`
- **Interactive Effects**: `floating`, `glow`, `shimmer`, `pulse`
- **Hover Animations**: Lift, scale, shadow, and gradient effects
- **Loading States**: Enhanced spinners with pulse effects

### **Performance Features**
- **Hardware Acceleration**: CSS3 transforms for smooth 60fps
- **Optimized Timing**: Cubic-bezier curves for natural motion
- **Staggered Delays**: Sequential animation timing
- **Reduced Motion**: Accessibility support

## 🚀 **Quick Start**

### **Prerequisites**
- **Node.js 18+** (for frontend)
- **Java 17** (for backend)
- **Docker & Docker Compose** (for infrastructure)
- **Maven 3.8+** (for backend)
- **Angular CLI 17** (for frontend)

### **1. Backend Setup**

```bash
# Navigate to backend directory
cd backend-java

# Start infrastructure services
docker-compose up -d

# Run CMS Service (port 8078)
cd services/cms
mvn spring-boot:run

# Run Discovery Service (port 8079) - in another terminal
cd services/cms-discovery
mvn spring-boot:run
```

### **2. Frontend Setup**

```bash
# Navigate to frontend directory
cd cms-frontend

# Install dependencies
npm install --legacy-peer-deps

# Start development server
npm start
```

### **3. Access the Application**

- **Frontend**: http://localhost:4200
- **CMS API**: http://localhost:8078/swagger-ui.html
- **Discovery API**: http://localhost:8079/swagger-ui.html
- **PostgreSQL**: localhost:5432
- **Elasticsearch**: localhost:9200
- **RabbitMQ**: localhost:15672

## 🔐 **Authentication**

### **Default Credentials**
```
Email: admin@thmanyah.io
Password: AdminPassword123!
```

### **Features**
- **JWT Tokens**: Secure authentication with automatic refresh
- **Role-based Access**: ADMIN and EDITOR roles
- **Session Management**: Persistent login state
- **API Security**: Protected endpoints with proper authorization

## 🎨 **Design System (Frontend)**

### **Color Palette**
- **Primary**: Dark theme (#1a1a1a, #2d2d2d)
- **Accent**: Blue gradient (#007bff, #0056b3)
- **Text**: White and gray hierarchy
- **Borders**: Subtle gray (#404040)

### **Typography**
- **Primary Font**: Cairo (Arabic support)
- **Fallback**: Segoe UI, Tahoma, Geneva, Verdana
- **Hierarchy**: Clear heading and body text sizing

### **Components**
- **Cards**: Elevated with hover animations
- **Buttons**: Gradient backgrounds with shimmer effects
- **Forms**: Dark theme with focus states
- **Navigation**: Smooth transitions and hover effects

## 📱 **Responsive Design**

### **Breakpoints**
- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: > 1024px

### **Features**
- **Mobile-First**: Optimized for mobile devices
- **Touch-Friendly**: Large touch targets
- **Adaptive Layout**: Flexible grid system
- **Performance**: Optimized animations for mobile

## 🏗️ **Architecture Overview**

### **Microservices Architecture**
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │   CMS Service    │    │ Discovery       │
│   (Angular)     │◄──►│   (Port 8078)    │◄──►│ Service         │
│   (Port 4200)   │    │                  │    │ (Port 8079)     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │                        │
                              ▼                        ▼
                       ┌──────────────┐        ┌──────────────┐
                       │ PostgreSQL   │        │ Elasticsearch│
                       │ (Port 5432)  │        │ (Port 9200)  │
                       └──────────────┘        └──────────────┘
                              │                        │
                              └────────┬───────────────┘
                                       ▼
                              ┌──────────────┐
                              │   RabbitMQ   │
                              │ (Port 5672)  │
                              └──────────────┘
```

### **Service Communication**
- **Synchronous**: REST APIs for direct communication
- **Asynchronous**: RabbitMQ for event-driven communication
- **Data Consistency**: Event sourcing for data synchronization

## 📚 **API Documentation**

### **CMS Service Endpoints**
- **Authentication**: `/api/v1/auth/*`
- **Shows**: `/api/v1/shows/*`
- **Users**: `/api/v1/users/*`
- **Import**: `/api/v1/import/*`

### **Discovery Service Endpoints**
- **Search**: `/api/v1/search/*`
- **Popular Content**: `/api/v1/popular/*`
- **Recent Content**: `/api/v1/recent/*`

### **External Provider Integration**
- **YouTube**: Video import and metadata extraction
- **Vimeo**: Video import with OAuth support
- **Mock Provider**: Development and testing

## 🚀 **Performance**

### **Frontend Optimizations**
- **Lazy Loading**: Component-based code splitting
- **Bundle Optimization**: Tree shaking and minification
- **Animation Performance**: Hardware-accelerated CSS
- **Image Optimization**: Responsive images and lazy loading

### **Backend Optimizations**
- **Database Indexing**: Optimized queries and indexes
- **Caching**: Redis for session and data caching
- **Connection Pooling**: Efficient database connections
- **Async Processing**: Background job processing

### **Performance Metrics**
- **Frontend**: First Contentful Paint < 2s
- **Backend**: API Response Time < 200ms
- **Search**: Query Response Time < 100ms
- **Database**: Query Execution < 50ms

## 🔧 **Development**

### **Code Quality**
- **Frontend**: TypeScript strict mode, ESLint, Prettier
- **Backend**: Java coding standards, Checkstyle, PMD
- **Testing**: Unit tests, integration tests, E2E tests
- **Documentation**: API docs, code comments, README files

### **Development Workflow**
1. **Feature Development**: Create feature branches
2. **Code Review**: Pull request reviews
3. **Testing**: Automated and manual testing
4. **Deployment**: Staging and production deployment

## 🧪 **Testing**

### **Frontend Testing**
- **Unit Tests**: Component and service testing
- **E2E Tests**: Cypress for end-to-end testing
- **Performance Testing**: Lighthouse audits

### **Backend Testing**
- **Unit Tests**: JUnit for service and controller testing
- **Integration Tests**: TestContainers for database testing
- **API Tests**: Postman collections for API testing

## 📦 **Deployment**

### **Frontend Deployment**
```bash
# Build for production
npm run build

# Deploy to static hosting
# (Netlify, Vercel, AWS S3, etc.)
```

### **Backend Deployment**
```bash
# Build JAR files
mvn clean package

# Run with Docker
docker-compose -f docker-compose.prod.yml up -d
```

## 🎉 **Recent Updates**

### **v2.0.0 - Animation & Styling Overhaul**
- ✨ **Complete Animation System**: Page transitions, hover effects, loading states
- 🎨 **Modern Dark Theme**: Thmanyah.com inspired design
- 🌍 **Arabic Content Support**: Full RTL and Arabic typography
- 📱 **Enhanced Responsiveness**: Mobile-optimized animations
- ⚡ **Performance Improvements**: Hardware-accelerated animations
- 🔧 **Code Quality**: TypeScript improvements and error fixes

### **Key Features Added**
- **Staggered Card Animations**: Sequential content loading
- **Interactive Hover Effects**: Cards lift and shimmer on hover
- **Floating Icons**: Gentle motion for visual elements
- **Smooth Form Transitions**: Bounce-in effects for forms
- **Enhanced Loading States**: Pulse and shimmer effects
- **Professional Color Scheme**: Dark theme with accent colors

## 🤝 **Contributing**

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes** in the appropriate directory (frontend or backend)
4. **Add tests** if applicable
5. **Follow coding standards** and documentation guidelines
6. **Submit a pull request**

### **Development Guidelines**
- **Frontend**: Follow Angular style guide and TypeScript best practices
- **Backend**: Follow Java coding conventions and Spring Boot patterns
- **Testing**: Maintain high test coverage
- **Documentation**: Update README files and API documentation

## 📄 **License**

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 **Acknowledgments**

- **Thmanyah.com**: Design inspiration and styling reference
- **Spring Team**: Backend framework and ecosystem
- **Angular Team**: Frontend framework and tooling
- **Bootstrap**: UI framework and components
- **Cairo Font**: Arabic typography support

## 📞 **Support**

- **Documentation**: Check individual service README files
- **Issues**: Report bugs and feature requests via GitHub issues
- **Discussions**: Use GitHub discussions for questions and ideas

---

**Built with ❤️ using Angular 17, Spring Boot 3, and modern web technologies**
