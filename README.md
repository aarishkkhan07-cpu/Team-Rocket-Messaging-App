# Team Rocket

Team Rocket is a full-stack Spring Boot chat application for account registration, login, user discovery, and private conversations between users.

## Features

- User registration with password confirmation and BCrypt password hashing
- Stateless JWT login and authentication
- User list with the current account excluded
- One-to-one message sending and conversation history
- Responsive static frontend for login, registration, users, and conversations
- File-backed H2 database for local use
- H2 web console

## Technology Stack

- Java 17
- Spring Boot 4.2
- Spring Web MVC
- Spring Security
- Spring Data JPA
- H2 Database
- JJWT
- Lombok
- Maven
- HTML, CSS, and JavaScript

## Project Structure

```text
src/
  main/
    java/com/example/team_rocket/
      config/       Security and password encoder configuration
      controller/   REST API endpoints
      dto/          API request and response models
      entity/       JPA User and Message entities
      repository/   Spring Data repositories
      security/     JWT services and authentication filter
      service/      Registration, messaging, and user logic
    resources/
      static/       Browser frontend
      application.properties
  test/java/      JUnit and Mockito test suite
```

## Prerequisites

- Java 17 or newer
- Maven (the included Maven Wrapper requires no global Maven installation)

## Quick Start

1. Open a terminal in the project root.
2. Start the application.
3. Open http://localhost:8080/.
4. Create an account, then log in.
5. Select another registered user to open a conversation.

## Run Locally

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS or Git Bash:

```bash
./mvnw spring-boot:run
```

The application is available at:

- Frontend: http://localhost:8080/
- Registration page: http://localhost:8080/register.html
- H2 console: http://localhost:8080/h2-console

## Test

```powershell
.\mvnw.cmd test
```

The test suite uses an isolated in-memory H2 database and mocks external application dependencies where appropriate.

Verified test coverage includes:

- Password mismatch and duplicate-user registration
- Successful and unsuccessful login
- JWT generation, extraction, and validation
- Sending trimmed messages
- Self-messaging and empty-message rejection
- Conversation retrieval
- Excluding the authenticated user from the user list
- Spring application context startup

## REST API

All endpoints use JSON.

### Register

`POST /api/auth/register`

```json
{
  "username": "alice",
  "password": "secret1",
  "confirmPassword": "secret1"
}
```

### Login

`POST /api/auth/login`

```json
{
  "username": "alice",
  "password": "secret1"
}
```

The response contains the user ID, username, and JWT token:

```json
{
  "userId": 1,
  "username": "alice",
  "token": "<jwt>"
}
```

### Protected Endpoints

Send the JWT using the Authorization header:

```text
Authorization: Bearer <jwt>
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/auth/me` | Return the authenticated username |
| GET | `/api/users` | List users other than the authenticated user |
| GET | `/api/messages/{userId}` | Get the conversation with a user |
| POST | `/api/messages` | Send a message |

Send-message request:

```json
{
  "receiverId": 2,
  "content": "Hello"
}
```

## Configuration

Application settings are in `src/main/resources/application.properties`.

Defaults are suitable for local demonstration. Override deployment values with environment variables:

| Environment variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC database URL | `jdbc:h2:file:./data/teamrocketdb` |
| `DB_USERNAME` | Database username | `teamrocket` |
| `DB_PASSWORD` | Database password | `teamrocket` |
| `JWT_SECRET` | Base64-encoded JWT signing secret | Development-only secret |
| `SERVER_PORT` | HTTP server port | `8080` |

PowerShell example:

```powershell
$env:DB_PASSWORD = "change-me"
$env:JWT_SECRET = "<base64-encoded-secret>"
.\mvnw.cmd spring-boot:run
```

For deployment, use a strong unique JWT secret and database credentials. Do not commit production secrets.

## Submission Checklist

- Java source, tests, Maven wrapper, and frontend assets are included
- `target/`, runtime H2 files, IDE files, and duplicate archives are excluded
- Registration and login work through the browser
- JWT-protected APIs require a valid bearer token
- The test suite passes with `mvnw test`
