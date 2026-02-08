---
name: Development Environment
description: Comprehensive guide to the development environment, including startup, testing, infrastructure, and configuration.
---

# Development Environment

This skill provides a complete overview of the development environment for the `interfero` project. Use this guide to understand how to start, test, and configure the application.

## 1. Technology Stack

- **Backend**: Java 25, Spring Boot 4.0.1, Spring Modulith.
- **Frontend**: Node.js v24.13.0, React 19, Vite 7, TypeScript 5.9.
- **Database**: PostgreSQL 18.1 / TimescaleDB 2.24.
- **Messaging**: Apache Pulsar 4.1.1.
- **Build Tools**: Maven (Backend), npm (Frontend).

## 2. Prerequisites

Ensure the following tools are installed:
- **Java JDK 25**
- **Node.js v24.13.0** (managed by Maven in production profile, but needed for local dev)
- **Docker & Docker Compose** (crucial for local infrastructure and tests)

## 3. Configuration (.env)

The application relies on environment variables defined in `.env`.
The file `.env` is used for:
1.  **Docker Compose**: Setting versions and credentials for containers.
2.  **Testcontainers**: Fallback configuration if `.testcontainers.env` is missing.

### Key Variables (from `.env`)

| Variable | Description | Default |
| :--- | :--- | :--- |
| `INTERFERO_DATABASE_VENDOR` | Database to use (`postgres` or `timescaledb`) | `postgres` |
| `POSTGRES_VERSION` | Version of PostgreSQL | `18.1` |
| `POSTGRES_USER` | Database username | `admin` |
| `POSTGRES_PASSWORD` | Database password | `secret` |
| `TIMESCALEDB_VERSION` | Version of TimescaleDB | `2.24.0-pg18` |
| `PULSAR_VERSION` | Version of Apache Pulsar | `4.1.1` |

## 4. Infrastructure (Docker Compose)

The `docker-compose.yaml` file defines the local development infrastructure.

### Services

1.  **postgres**
    *   **Image**: `postgres:${POSTGRES_VERSION}`
    *   **Port**: `5432`
    *   **Creds**: `interfero` / `${POSTGRES_USER}` / `${POSTGRES_PASSWORD}`
2.  **timescaledb**
    *   **Image**: `timescale/timescaledb:${TIMESCALEDB_VERSION}`
    *   **Port**: `5433` (mapped to internal 5432)
    *   **Role**: Alternative database choice.
3.  **pulsar-cluster-a**
    *   **Image**: `apachepulsar/pulsar-all:${PULSAR_VERSION}`
    *   **Ports**: `16650` (Broker), `18080` (HTTP)
    *   **Mode**: Standalone (`bin/pulsar standalone`)
4.  **pulsar-cluster-b**
    *   **Image**: `apachepulsar/pulsar-all:${PULSAR_VERSION}`
    *   **Ports**: `26650` (Broker), `28080` (HTTP)
    *   **Mode**: Standalone

**Note**: Two Pulsar clusters are simulated to test multi-cluster management capabilities.

## 5. Starting the Application

### Backend (Spring Boot)
Run the Spring Boot application using Maven or your IDE.
Ensure `docker-compose up` is running if you need the dependencies, OR rely on local configuration ensuring it points to the right ports.
*   **Command**: `mvn spring-boot:run`
*   **Production Build**: `mvn clean install -Pproduction` (This triggers frontend build).

### Frontend (Vite)
Located in `src/main/frontend`.
*   **Install Dependencies**: `npm install`
*   **Start Dev Server**: `npm run dev`
    *   This runs `orval` (API generation) followed by `vite`.
*   **Generate API Client**: `npm run generate:api`
    *   Uses `orval` to generate TypeScript clients from the OpenAPI spec.
*   **Build**: `npm run build`

## 6. Testing

### Backend Tests (Java)
The backend uses **Testcontainers** for integration testing. This ensures tests run against real infrastructure.

*   **Configuration**: `src/test/java/io/interfero/TestcontainersConfiguration.java`
*   **Behavior**:
    *   Reads `.testcontainers.env` or `.env`.
    *   Starts a **PostgreSQL** or **TimescaleDB** container based on `INTERFERO_DATABASE_VENDOR`.
    *   Starts **Two Pulsar Containers** (`cluster-a`, `cluster-b`) to match the docker-compose setup.
    *   Updates Spring properties dynamically (`interfero.database.url`, `pulsar.clusters...`).
*   **Running Tests**: `mvn test`

### Frontend Tests (Vitest)
*   **Runner**: Vitest
*   **Command**: `npm run test`
*   **UI Mode**: `npm run test:ui` (Opens a web UI for viewing test results)

## 7. Useful commands
- Start infrastructure: `docker-compose up -d`
- Stop infrastructure: `docker-compose down`
- Rebuild frontend from root: `mvn -Pproduction clean install`
