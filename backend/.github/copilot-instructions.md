# GitHub Copilot Instructions - Spring Boot Backend

You are an expert Senior Backend Developer specializing in Spring Boot, Java 21, and Modular Monolith architecture.
Follow these instructions strictly when generating code or refactoring.

## 1. Core Principles & Architecture
- **Architecture**: Modular Monolith with **Domain-Driven Design (DDD)**.
  - All business features must reside in `src/main/java/com/tpanh/server/modules/{module_name}`.
  - Core/Shared utilities reside in `src/main/java/com/tpanh/server/common`.
- **Design Patterns**: Adhere to **SOLID** principles.
  - **S**ingle Responsibility: One class, one purpose.
  - **O**pen/Closed: Open for extension, closed for modification.
  - **L**iskov Substitution: Subtypes must be substitutable for base types.
  - **I**nterface Segregation: Specific interfaces > General purpose ones.
  - **D**ependency Inversion: Depend on abstractions, not concretions.
- **Layered DDD Architecture**:
  - **Controller Layer**: Works strictly with **DTOs** (Request/Response). Configures mapping to Domain Models.
  - **Service Layer (Domain Logic)**: Works strictly with **Domain Models** (POJOs). Does NOT depend on JPA Entities or DTOs.
  - **Infrastructure/Data Layer**: Works with **JPA Entities**. Handles mapping between Domain Models and Entities.
  - **Strict Separation**: Controller never sees Entity. Service never sees Entity. Repository returns Entity but mapper converts to Domain before reaching Service.

## 2. Technology Stack & Coding Standards
- **Java Version**: 21 (Use modern features: `var`, `record`, Pattern Matching, switch expressions).
- **Framework**: Spring Boot 3.x+ / 4.x.
- **Dependency Injection**:
  - Always use **Constructor Injection**.
  - Use Lombok's `@RequiredArgsConstructor` for clean injection.
  - Avoid `@Autowired` on fields.
- **Mapping**: Use **MapStruct** for Entity <-> DTO conversion.
  - Do not write manual mapper methods unless complex logic is required.

## 3. Code Style & Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `AuthController`).
- **Methods/Variables**: camelCase (e.g., `findUserByEmail`, `isValid`).
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`).
- **Packages**: lowercase (e.g., `com.tpanh.server.modules.auth`).
- **No Magic Strings/Numbers**: Extract to constants or Enums.
- **Boolean Methods**: Prefix with `is`, `has`, `can` (e.g., `isActive()`).

## 4. Implementation Guidelines

### Entity Relationships (Manual FK — No JPA Annotations)
- **DO NOT** use `@ManyToOne`, `@OneToMany`, `@ManyToMany`, `@OneToOne` on new module Entities.
- Store foreign keys as **raw `UUID` fields** (e.g., `private UUID creatorId` instead of `@ManyToOne User creator`).
- Enforce FK constraints at the **database level** (Flyway migration with `REFERENCES`).
- Resolve relationships **manually in the Service layer** — query each repository separately when join data is needed.
- Response DTOs may contain nested/derived fields (e.g., `creatorName`) populated by the Service or Controller layer.
- Entity classes use suffix `Entity` (e.g., `TopicEntity`) to distinguish from Domain Models (e.g., `Topic`).
- **Legacy note**: Existing `auth` module entities (`User`, `RefreshToken`, `SocialAccount`) still use JPA relationship annotations — do not refactor them.

### Controller Layer (Interface Layer)
- **Role**: Entry point for HTTP requests.
- **Data**: Accepts **DTOs** (`RequestDTO`). Returns **DTOs** (`ResponseDTO`).
- **Behavior**:
  - Validates input (`@Valid`).
  - Maps `RequestDTO` -> `Domain Model`.
  - Calls Service with `Domain Model`.
  - Maps result `Domain Model` -> `ResponseDTO`.

### Service Layer (Domain Layer)
- **Role**: Business logic execution.
- **Data**: Operates strictly on **Domain Models** (POJOs).
- **Behavior**:
  - **No dependency on DTOs.**
  - **No dependency on JPA Entities.**
  - Performs business rules and validation.
  - Calls Repository Interface using `Domain Model`.

### Repository Layer (Infrastructure Layer)
- **Role**: Data persistence and retrieval.
- **Data**: Uses **JPA Entities** internally.
- **Behavior**:
  - Implements the Domain Repository Interface.
  - In implementation:
    - Receive `Domain Model`.
    - Convert `Domain` -> `Entity` (using `Entity.fromDomain()`).
    - Save `Entity`.
    - Convert `Entity` -> `Domain` (using `entity.toDomain()`).
  - Public repository interface ONLY returns/accepts Domain Models.

### Domain Model (Core)
- Pure Java Objects (POJOs) containing business logic.
- **No framework annotations** (`@Entity`, `@Table`) on fields.

### Mapping Strategy (MapStruct)
- **Principle**: Use **MapStruct** for all object mapping (Entity <-> Domain <-> DTO).
- **Location**: Create a `mapper` package in each module (e.g., `com.tpanh.server.modules.topic.mapper`).
- **Configuration**: Use `@Mapper(componentModel = "spring")` so mappers can be injected via Spring.
- **Methods**:
  - `toEntity(Domain domain)`: Domain -> Entity.
  - `fromEntity(Entity entity)`: Entity -> Domain.
  - `toDomain(RequestDTO dto, ...)`: DTO -> Domain.
  - `toResponse(Domain domain, ...)`: Domain -> ResponseDTO.
  - `updateEntity(UpdateDomain source, @MappingTarget Entity target)`: For partial updates using `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`.
- **Do not** write manual `toEntity()` or `fromEntity()` methods inside the Domain or Entity classes.

### DTOs (Data Transfer Objects)
- Prefer `record` for immutable DTOs (Java 14+).
  ```java
  public record UserResponse(String id, String email, String fullName) {}
  ```
- If using classes, use Lombok `@Data` or `@Value`.

## 5. Exception Handling
- Use Custom Exceptions extending `RuntimeException` (e.g., `ResourceNotFoundException`, `BusinessLogicException`).
- Do not catch global `Exception` in business logic. Let `GlobalExceptionHandler` handle it.
- Return standardized JSON error responses (e.g., `ErrorResponse`).

## 6. Database & Migrations
- Use **Flyway** for database schema changes.
- Place migration scripts in `src/main/resources/db/migration`.
- Naming convention: `V{VERSION}__{Description}.sql` (e.g., `V1__Init_Auth_Schema.sql`).
- Keywords in SQL should be uppercase.

## 7. Security (Spring Security)
- Use standard Spring Security 6+ filters and configuration.
- Stateless authentication with JWT (using `jjwt` library).
- Secure passwords using `BCryptPasswordEncoder`.

## 8. Development Workflow
- **Logging**: Use `@Slf4j`. Log meaningful messages.
  - `log.debug` for development info.
  - `log.info` for significant business events.
  - `log.error` for exceptions (include stack trace).
- **Testing**:
  - Write unit tests using JUnit 5 and Mockito.
  - Target high code coverage for Service and Utility classes.

## 9. Example Structure (Reference)
```java
// Service Interface
public interface UserService {
    UserDTO getUserById(Long id);
}

// Service Implementation
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserById(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }
}
```

## 10. Project Plan & Progress
- Always read the `.github/implementation-plan.md` file to understand the current project status, pending tasks, and architectural decisions before starting any new implementation.
- Follow the module breakdown strictly. Do not implement multiple modules at once.

