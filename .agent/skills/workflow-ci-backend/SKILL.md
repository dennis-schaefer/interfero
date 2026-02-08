---
name: Workflow CI Backend
description: Understands the backend CI/CD pipeline, including unit and integration tests, Java setup, and database matrices.
---

# Backend CI Workflow

The backend CI pipeline validates the Java Spring Boot application. It is defined in `.github/workflows/backend-tests.yml`.

## Workflows

### 1. Backend Tests (`backend-tests.yml`)
- **Triggers**: 
  - Push to any branch (`*`)
  - Pull Requests
  - Manual Dispatch (`workflow_dispatch`)
  - Reusable Call (`workflow_call`)
- **Jobs**:
  - `unit-tests`: Runs isolated unit tests.
  - `postgres-integration-tests`: Runs integration tests against a standard Postgres container.
  - `timescaledb-integration-tests`: Runs integration tests against a TimescaleDB container.

## Local Execution Guide

To simulate the CI steps locally, use the following commands from the project root.

### Prerequisites in CI
- **Java Platform**: OpenJDK 25 (Corretto distribution).
- **Maven Cache**: `~/.m2/repository` is cached between runs.

### running Unit Tests
The CI uses the `.github/actions/unit-tests` composite action.

**Command:**
```bash
mvn test --batch-mode -Dtest=*Test
```
*Note: This command explicitly filters for classes ending in `Test` to excludes integration tests.*

### Running Integration Tests
The CI uses the `.github/actions/integration-tests` composite action. Integration tests run with Testcontainers.

**Command:**
```bash
mvn test --batch-mode -Dtest=*IT
```
*Note: This command explicitly filters for classes ending in `IT`.*

**Configuration (Environment Variables):**
The integration tests support different database vendors/versions via environment variables (written to `.testcontainers.env` in CI):

1. **Standard Postgres (Default)**
   ```bash
   export INTERFERO_DATABASE_VENDOR=postgres
   export POSTGRES_VERSION=18.1
   mvn test -Dtest=*IT
   ```

2. **TimescaleDB**
   ```bash
   export INTERFERO_DATABASE_VENDOR=timescaledb
   export TIMESCALEDB_VERSION=2.24.0-pg18
   mvn test -Dtest=*IT
   ```

3. **Pulsar** (Default version: 4.1.1)
   ```bash
   export PULSAR_VERSION=4.1.1
   ```

## Agent Best Practices
- **Before Pushing**: ALWAYS run at least the unit tests locally.
- **Integration Issues**: If integration tests fail, check if your local Docker environment is running, as Testcontainers requires it.
- **Database Compatibility**: If modifying database schemas, verify functionality against BOTH Postgres and TimescaleDB configurations.
