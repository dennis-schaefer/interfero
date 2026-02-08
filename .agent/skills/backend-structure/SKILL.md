---
name: Backend Structure & Modularity
description: Guidelines for organizing the Spring Boot backend using feature-based modules and Spring Modulith principles.
---

# Backend Project Structure

The project follows a **Feature-Based Package Structure** (driven by Domain-Driven Design principles) rather than a layered package structure. This aligns with **Spring Modulith**.

## Package Organization

The root package is `io.interfero`.

All application code resides in `src/main/java/io/interfero`.
All test code resides in `src/test/java/io/interfero`.

Features are top-level packages under `io.interfero`.
Example: `io.interfero.clusters`, `io.interfero.tenants`, `io.interfero.security`.

### Internal Module Structure

Inside each feature module (e.g., `io.interfero.clusters`), organize classes by technical concern:

- **`controller`**: Spring `@RestController` classes defining the API.
- **`services`**: Business logic and domain services (`@Service`).
- **`repositories`**: Data access interfaces (`@Repository`) and implementations.
- **`domain`**: Domain entities (POJOs), value objects.
- **`dtos`**: Data Transfer Objects (Records or POJOs) for API communication.
- **`mappers`**: Components for mapping between DTOs and Domain objects.
- **`events`**: Application events (if applicable).
- **`configuration`**: Module-specific configuration (if applicable).

Do **NOT** mix classes in the root of the module package unless they are the aggregate root or main entry point (rarely needed).

## Dependency Rules

- Use `lombok` annotations (`@Slf4j`, `@RequiredArgsConstructor`, `@Getter`, `@ToString`) to reduce boilerplate.
- Use `org.jspecify.annotations.Nullable` for nullability annotations.

## Module Integration

- Modules should interact via public service methods or events.
- Avoid direct database access across module boundaries.
