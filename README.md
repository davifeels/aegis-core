# AegisCore

### Cloud Asset Management REST API

AegisCore is a backend application built with Java and Spring Boot to manage cloud infrastructure assets through a RESTful API. It provides CRUD operations, request validation, centralized exception handling, and PostgreSQL persistence.

The project focuses on backend engineering fundamentals, separation of responsibilities, data integrity, and automated testing.

## Features

- RESTful API for creating, retrieving, updating, and deleting cloud assets.
- Request validation with Jakarta Bean Validation.
- PostgreSQL persistence using Spring Data JPA and Hibernate.
- Centralized exception handling with structured HTTP responses.
- Automated integration tests covering API behavior and error scenarios.
- Environment-based database configuration to avoid hardcoded credentials.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access |
| Hibernate | Object-relational mapping |
| PostgreSQL 18 | Relational database |
| Jakarta Bean Validation | Input validation |
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

### Main Components

- **CloudAssetController:** Exposes the REST API endpoints.
- **CloudAssetService:** Implements the asset management operations.
- **CloudAssetRepository:** Provides database access through Spring Data JPA.
- **CloudAssetEntity:** Represents cloud assets in the database.
- **CloudAsset:** Defines the API data model and validation rules.
- **GlobalExceptionHandler:** Centralizes error handling and HTTP responses.

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

## Example Request

Register a cloud asset:

**Request**

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

**Successful response — `201 Created`**

```json
{
  "id": "aws-db-001",
  "name": "Production Database",
  "type": "DATABASE",
  "provider": "AWS",
  "region": "us-east-1"
}
```

## Error Handling

The API returns appropriate HTTP status codes for common error scenarios.

| Status | Description |
|---|---|
| `400 Bad Request` | Invalid input, validation failure, or malformed JSON |
| `404 Not Found` | Requested asset does not exist |
| `409 Conflict` | Asset registration conflicts with an existing resource |
| `201 Created` | Asset successfully registered |
| `204 No Content` | Asset successfully deleted |

Validation responses include field-level details to help clients identify invalid input.

## Getting Started

### Prerequisites

Install the following software:

- Java 25 or a compatible JDK
- PostgreSQL
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Clone the Repository

```bash
git clone https://github.com/davifeels/aegis-core.git
cd aegis-core
```

### 2. Create the PostgreSQL Databases

Connect to PostgreSQL using an administrative account and create the application user and databases.

```sql
CREATE USER aegiscore_user WITH PASSWORD 'CHANGE_ME';

CREATE DATABASE aegiscore OWNER aegiscore_user;

CREATE DATABASE aegiscore_test OWNER aegiscore_user;
```

Replace `CHANGE_ME` with a strong local password. Never commit real credentials to the repository.

### 3. Configure Environment Variables

On Linux or macOS, configure the database credentials in your shell:

```bash
export DB_USER=aegiscore_user
export DB_PASSWORD='your-local-password'
```

These variables are used by the application's database configuration.

Ensure PostgreSQL is running and accessible at `localhost:5432`.

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

The asset API will be available at:

```text
http://localhost:8080/api/v1/assets
```

## Testing

Run the full verification suite:

```bash
./mvnw clean verify
```

The test configuration uses the separate `aegiscore_test` database.

**Important:** The test profile uses `spring.jpa.hibernate.ddl-auto=create-drop`, which creates and drops database tables during the test lifecycle. Never point the test configuration at a database containing data you need to preserve.

The latest verified test run completed with **23 tests, 0 failures, and 0 errors**.

## Configuration

The application reads database credentials from environment variables.

| Variable | Purpose | Default |
|---|---|---|
| `DB_USER` | PostgreSQL username | `aegiscore_user` |
| `DB_PASSWORD` | PostgreSQL password | No default; must be provided |

The main application currently uses `spring.jpa.hibernate.ddl-auto=update`.

For production environments, consider using a dedicated database migration tool such as Flyway and reviewing schema changes before deployment.

## Engineering Practices

This project demonstrates practical backend development concepts:

- Separation of controllers, services, repositories, and entities.
- REST conventions and meaningful HTTP status codes.
- Request validation with Jakarta Bean Validation.
- Transaction management for database operations.
- Centralized exception handling.
- Environment-based configuration for sensitive values.
- Automated integration testing.
- Version control using Git.

## Roadmap

Potential future improvements include:

- OpenAPI documentation and Swagger UI.
- Authentication and role-based authorization.
- Database migrations with Flyway.
- Docker and Docker Compose support.
- Continuous integration with GitHub Actions.
- Cloud deployment and infrastructure monitoring.
- Pagination, filtering, and sorting for asset queries.

These are planned improvements and are not represented as currently implemented features.

## Author

**Davi Anderson**

Backend Developer | Java · Spring Boot · REST APIs · PostgreSQL

- GitHub: [@davifeels](https://github.com/davifeels)
- LinkedIn: [Davi Anderson](https://www.linkedin.com/in/davi-tech/)
- Portfolio: [davifeels.github.io/portfolio](https://davifeels.github.io/portfolio)

## License

No license has been specified for this repository. All rights are reserved unless a license is added.
