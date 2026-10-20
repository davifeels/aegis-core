# AegisCore

### Cloud Asset Management REST API

AegisCore is a backend application built with Java and Spring Boot to manage cloud infrastructure assets through a RESTful API. It provides CRUD operations, request validation, centralized exception handling, and PostgreSQL persistence.

The project focuses on backend engineering fundamentals, clean separation of responsibilities, data integrity, and automated testing.

## Features

- **RESTful API:** Create, retrieve, update, and delete cloud assets.
- **Input validation:** Enforces required fields, length constraints, and supported asset types and cloud providers.
- **PostgreSQL persistence:** Stores cloud asset records using Spring Data JPA.
- **Centralized error handling:** Returns structured HTTP error responses for validation failures, malformed requests, missing resources, and conflicts.
- **Automated testing:** Integration tests cover API behavior, validation, persistence, and error scenarios.
- **Environment-based configuration:** Database credentials are supplied through environment variables instead of being hardcoded.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web | REST API endpoints |
| Spring Data JPA | Database access and persistence |
| Hibernate | ORM and entity management |
| PostgreSQL 18 | Relational database |
| Jakarta Bean Validation | Request validation |
| Maven Wrapper | Build and dependency management |
| JUnit and Spring Boot Test | Automated testing |
| Git and GitHub | Version control |

## Architecture

The application separates HTTP handling, business logic, persistence, and error handling.

```text
HTTP Client
    |
    v
CloudAssetController
    |
    v
CloudAssetService
    |
    v
CloudAssetRepository
    |
    v
CloudAssetEntity
    |
    v
PostgreSQL
```

**Main components**

- `CloudAssetController` — exposes the REST endpoints.
- `CloudAssetService` — handles asset registration, retrieval, updates, and deletion.
- `CloudAssetRepository` — provides database operations through Spring Data JPA.
- `CloudAssetEntity` — maps cloud assets to the database.
- `CloudAsset` — defines the API data model and validation constraints.
- `GlobalExceptionHandler` — centralizes API error responses.

## API Reference

Base URL:

```text
http://localhost:8080/api/v1/assets
```

| Method | Endpoint | Description | Success |
|---|---|---|---|
| `POST` | `/api/v1/assets` | Register a cloud asset | `201 Created` |
| `GET` | `/api/v1/assets` | List all assets | `200 OK` |
| `GET` | `/api/v1/assets/{id}` | Retrieve an asset by ID | `200 OK` |
| `PUT` | `/api/v1/assets/{id}` | Update an existing asset | `200 OK` |
| `DELETE` | `/api/v1/assets/{id}` | Delete an asset | `204 No Content` |

### Supported Asset Types

- `DATABASE`
- `VIRTUAL_MACHINE`
- `STORAGE`
- `NETWORK`

### Supported Cloud Providers

- `AWS`
- `AZURE`
- `GCP`

### Example Request

Register a cloud asset:

```http
POST /api/v1/assets
Content-Type: application/json
```

```json
{
  "id": "aws-db-001",
  "name": "Production Database",
  "type": "DATABASE",
  "provider": "AWS",
  "region": "us-east-1"
}
```

Example response:

```http
HTTP/1.1 201 Created
```

```json
{
  "id": "aws-db-001",
  "name": "Production Database",
  "type": "DATABASE",
  "provider": "AWS",
  "region": "us-east-1"
}
```

### Error Handling

The API uses appropriate HTTP status codes to communicate request outcomes.

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid input, validation failure, or malformed JSON |
| `404 Not Found` | Requested asset does not exist |
| `409 Conflict` | Asset registration conflicts with an existing resource |
| `201 Created` | Asset successfully registered |
| `204 No Content` | Asset successfully deleted |

Validation errors include field-level details to help clients identify invalid input.

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 25 or a compatible JDK
- PostgreSQL
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Clone the repository

```bash
git clone https://github.com/davifeels/aegis-core.git
cd aegis-core
```

### 2. Create the PostgreSQL database

Connect to PostgreSQL using an administrative account and create a dedicated application user and database.

```sql
CREATE USER aegiscore_user WITH PASSWORD 'CHANGE_ME';
CREATE DATABASE aegiscore OWNER aegiscore_user;
```

Replace `CHANGE_ME` with a strong local password. Do not commit real credentials to the repository.

### 3. Configure environment variables

On Linux or macOS, set the database credentials in your shell:

```bash
export DB_USER=aegiscore_user
export DB_PASSWORD='your-local-password'
```

These variables are used by the application's database configuration. The database must be running and accessible on `localhost:5432`.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Testing

Run the complete verification suite:

```bash
./mvnw clean verify
```

The test suite uses a separate PostgreSQL database named `aegiscore_test`, configured through `src/test/resources/application.properties`.

Create the test database and grant access to the configured application user before running the tests:

```sql
CREATE DATABASE aegiscore_test OWNER aegiscore_user;
```

Set the same `DB_USER` and `DB_PASSWORD` environment variables used by the application.

**Important:** The test configuration uses `spring.jpa.hibernate.ddl-auto=create-drop`. The test database is intended exclusively for automated tests; do not point it at a database containing data you need to preserve.

The latest verified test run completed with **23 tests, 0 failures, and 0 errors**.

## Configuration

The application reads its database settings from environment variables.

| Variable | Purpose | Default |
|---|---|---|
| `DB_USER` | PostgreSQL username | `aegiscore_user` |
| `DB_PASSWORD` | PostgreSQL password | No default; must be provided |

The main application uses Hibernate schema updates through `spring.jpa.hibernate.ddl-auto=update`. For production deployments, use a deliberate database migration strategy and review schema changes before applying them.

## Engineering Practices

This project demonstrates practical backend development concepts, including:

- Separation of controllers, services, repositories, and entities.
- REST conventions and meaningful HTTP status codes.
- Bean Validation for incoming requests.
- Transaction boundaries for database operations.
- Centralized exception handling.
- Environment-based configuration for sensitive values.
- Automated integration testing.
- Version control with Git.

## Roadmap

Potential future improvements include:

- API documentation with OpenAPI and Swagger UI.
- Authentication and role-based authorization.
- Database migrations with Flyway.
- Docker and Docker Compose deployment.
- Continuous integration with GitHub Actions.
- Cloud deployment and infrastructure monitoring.
- Pagination, filtering, and sorting for asset queries.

These items are planned improvements, not features currently claimed as implemented.

## Author

**Davi Anderson**

Backend Developer | Java · Spring Boot · REST APIs · PostgreSQL

- GitHub: [@davifeels](https://github.com/davifeels)
- LinkedIn: [Davi Anderson](https://www.linkedin.com/in/davi-tech/)
- Portfolio: [davifeels.github.io/portfolio](https://davifeels.github.io/portfolio)

## License

No license has been specified yet. All rights are reserved unless a license is added to this repository.
