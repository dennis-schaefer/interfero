---
name: Backend Service Implementation
description: Guidelines for implementing Service layer logic in Spring Boot.
---

# Service Layer Guidelines

Services contain the business logic of the application.

## Class Definition

- Annotate with `@Service`.
- Annotate with `@Slf4j` for logging.
- Annotate with `@RequiredArgsConstructor` (Lombok) for dependency injection.
- Class names should end with `Service` (e.g., `ClusterService`).

## Logic and Responsibilities

- **Transactional**: Use `@Transactional` only where necessary (Project uses `JdbcClient` extensively, so manage transactions deliberately).
- **Validation**: Perform business validation (e.g., duplicate checks, state consistency).
- **Communication**: Interact with `Repositories` for data access.
- **Conversion**: Generally deals with Domain Entities, NOT DTOs. DTO conversion happens in the Controller layer.

## Logging

- Use `log.debug` for tracing flow control and internal details.
- Use `log.info` for significant lifecycle events (e.g., "Creating new cluster", "Startup complete").
- Use `log.error` for exceptions.

## Example

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class ExampleService {

    private final ExampleRepository repository;

    public ExampleEntity create(ExampleEntity entity) {
        log.info("Creating new example: {}", entity.getName());
        if (repository.existsByName(entity.getName())) {
            throw new IllegalArgumentException("Name already exists");
        }
        var saved = repository.save(entity);
        log.debug("Saved example with id: {}", saved.getId());
        return saved;
    }
}
```
