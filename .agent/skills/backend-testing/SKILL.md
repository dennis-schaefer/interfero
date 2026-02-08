---
name: Backend Testing
description: Guidelines for implementing unit and integration tests using JUnit 5, Mockito, and Testcontainers.
---

# Testing Guidelines

We use **JUnit 5**, **Mockito**, and **Testcontainers** for testing.

## Test Structure

- Match the package structure of the main code: `src/test/java/io/interfero/...`
- **Integration Tests**: End with `IT.java` (e.g., `ClustersIT.java`).
- **Unit Tests**: End with `Test.java`.

## Integration Tests (`*IT.java`)

- Use `@SpringBootTest` to load the full context if necessary.
- Use **Testcontainers** for database and external service dependencies (Postgres, Pulsar, etc.).
- See `io.interfero.TestcontainersConfiguration` for base configuration.

## Unit Tests

- Use `@ExtendWith(MockitoExtension.class)` for pure unit tests using mocks.
- Use `@WebMvcTest` for Controller slice tests.

## Coding Standards

- Use descriptive test method names (e.g., `shouldReturnNotFoundWhenIdDoesNotExist`).
- Follow **Arrange-Act-Assert** pattern.

## Spring Modulith

- The project uses Spring Modulith capabilities.
- Ensure `ModulithTest.java` passes to verify module structure compliance.
