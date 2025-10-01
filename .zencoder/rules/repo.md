---
description: Repository Information Overview
alwaysApply: true
---

# WCFC Groundschool Materials Website Information

## Summary
A learning system for the Wings of Carolina flying club that provides groundschool materials and resources. The application consists of a Java backend using Dropwizard framework and a Svelte frontend.

## Structure
- **client/**: Svelte frontend application
- **integration-tests/**: Integration test scripts and data
- **src/**: Java backend source code
- **scripts/**: Utility scripts for deployment and operations
- **.github/**: GitHub workflows and configuration
- **.mvn/**: Maven configuration files

## Main Repository Components
- **Backend Service**: Java-based Dropwizard application serving API endpoints
- **Frontend Client**: Svelte-based web application
- **Docker Deployment**: Configuration for containerized deployment

## Output Directories
Note that these directories are in .gitignore, so you will normally be denied access to them.
- **target/**: Compiled Java classes and output from the Maven build process
- **docker/**: Dockerfile and build files, mostly copied from src/main/resources

### Backend (Java Dropwizard)
**Configuration File**: pom.xml

#### Language & Runtime
**Language**: Java
**Version**: Java 21
**Build System**: Maven
**Package Manager**: Maven

#### Dependencies
**Main Dependencies**:
- Dropwizard 5.0.0 (core, client, assets, auth, forms)
- Jackson 2.20.0
- MongoDB Driver 5.6.0
- Morphia 2.5.0
- JWT 0.13.0
- Retrofit 3.0.0
- Google APIs (Gmail, Auth)

## Build & Installation
```bash
# Build the application
make

# Build Docker image
make build

# Run integration tests
make integration-tests
```

### Docker
**Base Image**: azul/zulu-openjdk-alpine:21-latest
**Configuration**: Uses Alpine Linux with Azul Zulu OpenJDK 21
**Dockerfile**: src/main/resources/Dockerfile

### Testing
**Unit Tests**: As of now there are no unit tests for this app.
**Integration Tests**: There are end-to-end integration tests in `integration-tests/`.  These use Playwright for browser automation and WireMock for mocking external APIs.
**Run Command**:
```bash
make integration-tests
```

### Frontend (Svelte)
**Configuration File**: client/package.json

#### Language & Runtime
**Language**: JavaScript
**Version**: ES Modules
**Build System**: Vite
**Package Manager**: npm

#### Dependencies
**Main Dependencies**:
- Svelte 5.39.6
- SvelteKit 2.43.5
- Vite 7.1.7
- Compression 1.7.4
- Polka 1.0.0-next.25
- Sirv 3.0.2

**Development Dependencies**:
- TypeScript 5.3.0
- Prettier 3.1.0
- Svelte Check 4.3.2

#### Testing
**Framework**: Cypress
**Test Location**: client/cypress
**Configuration**: client/cypress.json
**Run Command**:
```bash
cd client
npm run test
```

## Deployment
This app is expected to run in Google Cloud Run.  In its production deployment, it does not have a long-running
process; instead, its container is launched on demand when there is traffic.  As a result, internal maintenance
tasks are scheduled opportunistically at app start.
