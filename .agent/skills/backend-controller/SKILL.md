---
name: Backend Controller Implementation
description: Guidelines for implementing REST Controllers in Spring Boot for this project.
---

# REST Controller Implementation Guidelines

Controllers in this project use Spring Web MVC with specific conventions for logging, response handling, and dependency injection.

## Class Definition

- Annotate with `@RestController`.
- Annotate with `@RequestMapping("/api/<resource>")` to define the base path.
- Annotate with `@Slf4j` for logging.
- Annotate with `@RequiredArgsConstructor` (Lombok) for constructor-based dependency injection.
- Class names should end with `Controller` (e.g., `ClusterController`).

## Method Structure

- **Return Type**: Always return `ResponseEntity<T>` to have full control over HTTP status codes and headers.
- **Mapping**: Use specific mapping annotations: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`.
- **Validation**: Use `@Valid` on request bodies (`@RequestBody`) to enforce Jakarta Validation constraints defined in DTOs.

## Logging

- Log entry into the method using `log.debug`.
- Log the return value/status using `log.debug` before returning.
- **Format**:
  ```java
  @GetMapping("/{id}")
  ResponseEntity<MyDto> getById(@PathVariable String id) {
      log.debug("HTTP GET '/api/resource/{}' called", id);
      // ... logic ...
      log.debug("HTTP GET '/api/resource/{}' returned: {}", id, response);
      return response;
  }
  ```

## DTOs and Mapping

- Controllers should **never** return Domain Entities directly.
- Accept DTOs as input and return DTOs as output.
- Use a dedicated **Mapper** component (in `mappers` subpackage) to convert between DTOs and Entities.
- Inject Mappers via constructor.

## Example

```java
@Slf4j
@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService service;
    private final ExampleMapper mapper;

    @GetMapping
    public ResponseEntity<List<ExampleDto>> getAll() {
        log.debug("HTTP GET '/api/examples' called");
        var entities = service.findAll();
        var dtos = entities.stream().map(mapper::toDto).toList();
        var response = ResponseEntity.ok(dtos);
        log.debug("HTTP GET '/api/examples' returned: {}", response);
        return response;
    }
}
```
