---
name: Backend Persistence Implementation
description: Guidelines for implementing data access using JdbcClient and SQL.
---

# Persistence Layer Guidelines

This project primarily uses **Spring `JdbcClient`** (native SQL) instead of Hibernate/JPA.

## Repositories

- **Interface**: Define a repository interface in the `repositories` package (e.g., `ClusterRepository`).
- **Implementation**: Implement the interface in a class ending with `JdbcRepository` (e.g., `ClusterJdbcRepository`).
    - Annotate implementation with `@Repository`.
    - Annotate with `@RequiredArgsConstructor`.
    - Use `@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "true")` if the feature is optional.

## implementing Data Access

- Inject `org.springframework.jdbc.core.simple.JdbcClient`.
- Write explicit SQL queries using text blocks (`""" ... """`).
- Use named parameters (e.g., `:id`, `:name`).
- Map results to Domain Entities manually or using `query(Class).set()/list()/optional()`.

## Domain Entities

- Entities are **POJOs**, not `@Entity` classes.
- Use Lombok: `@Getter`, `@EqualsAndHashCode`, `@ToString`.
- Construct via constructor.
- Use `org.jspecify.annotations.Nullable` for optional fields.

## Example Implementation

```java
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "true")
public class ExampleJdbcRepository implements ExampleRepository {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<ExampleEntity> findById(String id) {
        var sql = "SELECT * FROM example WHERE id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(ExampleEntity.class)
                .optional();
    }
}
```
