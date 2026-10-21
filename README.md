# AegisCore

AegisCore is a REST API for managing cloud assets, built with Java 25, Spring Boot, Spring Data JPA, and PostgreSQL. The project demonstrates backend architecture, API security, database migrations, automated testing, and containerized deployment with Docker Compose.

## Features

- RESTful CRUD operations for cloud assets.
- Request validation with Jakarta Bean Validation.
- PostgreSQL persistence using Spring Data JPA.
- Database versioning with Flyway.
- Centralized exception handling and meaningful HTTP responses.
- HTTP Basic authentication with BCrypt password encoding.
- Public OpenAPI documentation through Swagger UI.
- Automated functional and security tests.
- Dockerized API and PostgreSQL services.

## Technology Stack

- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL 18
- Flyway
- Maven Wrapper
- Docker and Docker Compose
- OpenAPI / Swagger UI

## Architecture

The application follows a layered architecture:

- **CloudAssetController:** Exposes the REST API endpoints.
- **CloudAssetService:** Implements asset management operations.
- **CloudAssetRepository:** Provides database access through Spring Data JPA.
- **CloudAssetEntity:** Represents cloud assets in the database.
- **CloudAsset:** Defines the API data model and validation rules.
- **GlobalExceptionHandler:** Centralizes error handling and HTTP responses.

## API Reference

### Base URL

When running with Docker Compose:

`http://localhost:8081`

When running locally:

`http://localhost:8080`

### Endpoints

All asset endpoints require HTTP Basic authentication.

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

The request requires HTTP Basic authentication. A successful registration returns `201 Created` with the created asset.

## Authentication and API Documentation

The application protects asset endpoints with HTTP Basic authentication. The administrator credentials are configured through environment variables:

- `AEGIS_ADMIN_USERNAME`
- `AEGIS_ADMIN_PASSWORD`

Passwords are encoded using BCrypt. Do not commit real credentials or `.env` files to the repository.

The OpenAPI specification is publicly accessible at:

`http://localhost:8081/v3/api-docs`

Swagger UI is available at:

`http://localhost:8081/swagger-ui.html`

Replace port `8081` with `8080` when running the application locally.

HTTP Basic authentication should only be used over HTTPS in production. The included local Docker setup is intended for development and testing.

## Error Handling

| Status | Description |
|---|---|
| `400 Bad Request` | Invalid input, validation failure, or malformed JSON |
| `401 Unauthorized` | Missing or invalid authentication credentials |
| `404 Not Found` | Requested asset does not exist |
| `409 Conflict` | Asset registration conflicts with an existing resource |
| `201 Created` | Asset successfully registered |
| `204 No Content` | Asset successfully deleted |

Validation responses include field-level details to help clients identify invalid input.

## Getting Started with Docker

Docker Compose starts the API and its PostgreSQL database.

### Prerequisites

- Git
- Docker Engine
- Docker Compose

### 1. Clone the Repository

```bash
git clone https://github.com/davifeels/aegis-core.git
cd aegis-core
```

### 2. Configure Environment Variables

Create a local environment file from the example:

```bash
cp .env.example .env
```

Edit `.env` and replace the placeholder values with strong, unique passwords for the database and administrator account.

Keep `.env` private. It is excluded from version control.

### 3. Build and Start the Services

```bash
sudo docker compose up -d --build
```

Check service status:

```bash
sudo docker compose ps
```

The API should be available at `http://localhost:8081`, and PostgreSQL is published on `127.0.0.1:5433`.

The database uses a persistent Docker volume so its data can survive container recreation.

### 4. Verify the API

Check that unauthenticated requests are rejected:

```bash
curl -i http://localhost:8081/api/v1/assets
```

Expected response: `401 Unauthorized`.

Check that the API documentation is accessible:

```bash
curl -i http://localhost:8081/v3/api-docs
```

Expected response: `200 OK`.

Test an authenticated request without printing credentials:

```bash
set -a
source .env
set +a

curl -i \
  -u "$AEGIS_ADMIN_USERNAME:$AEGIS_ADMIN_PASSWORD" \
  http://localhost:8081/api/v1/assets

unset DB_PASSWORD AEGIS_ADMIN_PASSWORD
```

Expected response: `200 OK`, assuming the endpoint is available and the credentials match the running application.

### 5. Stop the Services

```bash
sudo docker compose down
```

This stops and removes the containers while preserving the named database volume. Do not use `docker compose down -v` unless you intentionally want to remove the persistent volume and its database data.

## Running Locally Without Docker

### Prerequisites

- Java 25 or a compatible JDK
- PostgreSQL
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Configure PostgreSQL

Create a PostgreSQL user and an application database using an administrative account:

```sql
CREATE USER aegiscore_user WITH PASSWORD 'CHANGE_ME';
CREATE DATABASE aegiscore OWNER aegiscore_user;
```

Replace `CHANGE_ME` with a strong local password. If the user or database already exists, reuse the existing configuration instead of creating duplicates.

### 2. Configure Environment Variables

On Linux or macOS:

```bash
export DB_USER=aegiscore_user
export DB_PASSWORD='your-local-password'
export AEGIS_ADMIN_USERNAME='aegis-admin'
export AEGIS_ADMIN_PASSWORD='your-admin-password'
```

Ensure the local PostgreSQL instance is available at `localhost:5432`.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8080`.

## Testing

Run the automated test suite:

```bash
./mvnw test
```

The test configuration uses the separate `aegiscore_test` database. Ensure this database exists and that the test connection URL and credentials point to the correct PostgreSQL instance.

For example, when using the Docker PostgreSQL service, first create the test database if it does not exist, then run:

```bash
set -a
source .env
set +a

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/aegiscore_test ./mvnw test

unset DB_PASSWORD AEGIS_ADMIN_PASSWORD
```

The test configuration disables Flyway and uses `spring.jpa.hibernate.ddl-auto=create-drop`. Tests may create and drop tables in the configured test database. **Never point the test configuration at a database containing data you need to preserve.**

The latest verified test run completed with **30 tests, 0 failures, and 0 errors**.

## Configuration

| Variable | Purpose |
|---|---|
| `DB_USER` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `AEGIS_ADMIN_USERNAME` | HTTP Basic administrator username |
| `AEGIS_ADMIN_PASSWORD` | HTTP Basic administrator password |
| `SPRING_DATASOURCE_URL` | Optional Spring datasource URL override |

The main application uses Flyway for database migrations and Hibernate schema validation (`spring.jpa.hibernate.ddl-auto=validate`).

New database migrations should be added as versioned SQL files in `src/main/resources/db/migration`, using the next available migration version.

## Engineering Practices

This project demonstrates:

- Separation of controllers, services, repositories, and entities.
- REST conventions and meaningful HTTP status codes.
- Request validation and centralized exception handling.
- Transactional database operations.
- HTTP Basic authentication and password hashing.
- Version-controlled database migrations.
- Automated functional and security testing.
- Containerization and persistent database storage.
- Environment-based configuration without committing secrets.

## Future Improvements

- Add HTTPS and production-grade secret management.
- Introduce role-based authorization and more granular access control.
- Add CI/CD automation for tests, image builds, and security checks.
- Add observability through structured logging and health metrics.
- Expand integration tests and API documentation.

## License

Add a license file if you intend to distribute this project under a specific open-source license.
