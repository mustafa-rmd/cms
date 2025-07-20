# CMS Frontend

A modern Angular frontend application for the Content Management System (CMS) backend.

## Features

- **Authentication**: Login/logout functionality with JWT tokens
- **Content Discovery**: Advanced search and filtering capabilities
- **Show Management**: CRUD operations for shows (requires authentication)
- **User Management**: Admin-only user administration
- **Import Management**: Import content from external providers
- **Responsive Design**: Built with Bootstrap 5 for mobile-first design

## Architecture

The frontend is built with Angular 17 and follows a modular architecture:

### Services
- **AuthService**: Handles authentication and token management
- **ShowService**: Manages show CRUD operations (CMS Service - Port 8078)
- **SearchService**: Handles content discovery (Discovery Service - Port 8079)
- **ImportService**: Manages import operations from external providers

### Components
- **Home**: Landing page with popular and recent shows
- **Login**: Authentication form
- **Search**: Advanced search interface with filters
- **Shows**: Show management interface
- **Users**: User administration (Admin only)
- **Import**: Import management interface

## Backend Services

The frontend communicates with two backend services:

1. **CMS Service (Port 8078)**: Heavy write service with authentication
   - Show CRUD operations
   - User management
   - Import operations
   - Authentication

2. **Discovery Service (Port 8079)**: Read-heavy service without authentication
   - Content search and discovery
   - Public read-only API

## Getting Started

### Prerequisites

- Node.js (v20.5.1 or higher)
- npm (v9.8.0 or higher)
- Angular CLI 17

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
ng serve
```

3. Open your browser and navigate to `http://localhost:4200`

### Build for Production

```bash
ng build
```

## Configuration

The application is configured to connect to:
- CMS Service: `http://localhost:8078`
- Discovery Service: `http://localhost:8079`

## Authentication

The application uses JWT tokens for authentication. Demo credentials:
- Email: `admin@thmanyah.io`
- Password: `AdminPassword123!`

## Features in Detail

### Search Functionality
- Full-text search across show titles and descriptions
- Filter by type, language, provider, tags, categories
- Duration and rating filters
- Date range filtering
- Sorting options

### Show Management
- View all shows with pagination
- Create, edit, and delete shows
- Filter shows by various criteria
- Bulk operations

### User Management (Admin Only)
- View all users
- Create and edit users
- Role-based access control

### Import Management
- Import content from YouTube and Vimeo
- Monitor import job status
- Cancel running imports

## Technologies Used

- **Angular 17**: Frontend framework
- **Bootstrap 5**: CSS framework for responsive design
- **Bootstrap Icons**: Icon library
- **RxJS**: Reactive programming
- **TypeScript**: Type-safe JavaScript

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── auth/
│   │   │   └── login/
│   │   ├── home/
│   │   ├── search/
│   │   ├── shows/
│   │   ├── users/
│   │   └── import/
│   ├── models/
│   ├── services/
│   ├── interceptors/
│   └── app.component.*
├── assets/
└── styles.scss
```

## Development

### Adding New Components

```bash
ng generate component components/component-name
```

### Adding New Services

```bash
ng generate service services/service-name
```

## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure backend services are running and configured for CORS
2. **Authentication Issues**: Check JWT token configuration in backend
3. **Build Errors**: Update Node.js and npm to compatible versions

### Debug Mode

Run the application in debug mode:
```bash
ng serve --configuration development
```

## Contributing

1. Follow Angular style guide
2. Use TypeScript strict mode
3. Write unit tests for new features
4. Ensure responsive design
5. Follow Bootstrap conventions

## License

This project is part of the CMS system and follows the same license terms.
