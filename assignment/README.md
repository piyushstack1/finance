# Finance Dashboard Backend API

A Spring Boot-based backend service for managing financial records with role-based access control, user management, and dashboard analytics. The system enforces different permission levels for different user roles to ensure secure and controlled access to financial data.

## Project Overview

This backend implements a complete financial record management system. It handles user authentication using JWT tokens, enforces role-based access control, manages financial records with CRUD operations, provides aggregated dashboard analytics, and maintains a comprehensive user management system for administrators. The system is built with security and data integrity as primary concerns.

## Technology Stack

- Java 21
- Spring Boot 4.0.5
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- JJWT 0.12.3

## Project Structure

```
src/main/java/com/finance/assignment/
├── config/
│   ├── AppConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── FinancialRecordController.java
│   └── UserController.java
├── dto/
│   ├── AuthResponseDTO.java
│   ├── DashboardResponseDTO.java
│   ├── LoginRequestDTO.java
│   ├── PageResponseDTO.java
│   ├── RecordRequestDTO.java
│   ├── RecordResponseDTO.java
│   ├── SignupRequestDTO.java
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── entity/
│   ├── FinancialRecord.java
│   ├── RecordType.java
│   ├── Role.java
│   ├── Status.java
│   └── User.java
├── exception/
│   ├── CustomException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── FinancialRecordRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
└── service/
    ├── AuthService.java
    ├── DashboardService.java
    ├── FinancialRecordService.java
    └── UserService.java
```

## Database Setup

The application uses PostgreSQL as the primary database. Before running the application, ensure PostgreSQL is installed and running on your system.

### Database Configuration

Database credentials are configured in the `application.properties` file:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=123
spring.datasource.driver-class-name=org.postgresql.Driver
```

You can override these values using environment variables:

- `DB_URL` - PostgreSQL connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

### Creating the Database

Create the PostgreSQL database before starting the application:

```sql
CREATE DATABASE finance_db;
```

Hibernate automatically creates and updates tables based on entity definitions when the application starts with `ddl-auto=update` configuration.

## Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.6 or later
- PostgreSQL 12 or later

### Installation and Running

1. Extract the project to your desired location.

2. Navigate to the project directory:
```bash
cd assignment
```

3. Ensure PostgreSQL database is created and running.

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8081`

Or run using the compiled JAR:
```bash
java -jar target/assignment-0.0.1-SNAPSHOT.jar
```

## Role-Based Access Control

The system implements three user roles with distinct permission levels:

### VIEWER
Users with this role can view dashboard summaries and financial records but cannot create, modify, or delete records. They also cannot access user management features.

### ANALYST
Analysts have read and write access to financial records. They can view dashboard data, create new records, and update or delete existing records. They cannot manage users or access administrative endpoints.

### ADMIN
Administrators have full system access. They can manage all financial records, create and modify users, assign roles, control user status, and view all dashboard data.

## Authentication and Authorization

### User Signup

When a user registers through the signup endpoint, the system creates a new user with VIEWER role and ACTIVE status. The password is hashed using BCrypt before storage, and a JWT token is immediately generated and returned for session management.

### User Login

During login, credentials are validated against the database. The system checks if the account is active and only generates a token for active accounts. Invalid credentials result in an appropriate error response. Tokens are valid for 24 hours from generation.

### Token Usage

Include the JWT token in the Authorization header of requests to protected endpoints:

```
Authorization: Bearer <jwt_token>
```

Without a valid token, requests to protected endpoints are rejected with a 401 Unauthorized response.

## API Endpoints

### Authentication APIs

- `POST /auth/signup` - Register a new user with name, email, and password. Returns JWT access token and assigns VIEWER role by default.
- `POST /auth/login` - Authenticate with email and password. Returns JWT access token valid for 24 hours if credentials are valid and account is active.

### Financial Records APIs

- `POST /records` - Create a new financial record with amount, type, category, date, and description. Requires ADMIN or ANALYST role.
- `GET /records` - Retrieve paginated list of financial records with optional filtering by type and category. Supports page and size query parameters. Requires authentication.
- `PUT /records/{id}` - Update an existing record by ID with new values. Requires ADMIN or ANALYST role.
- `DELETE /records/{id}` - Delete a financial record by ID. Requires ADMIN or ANALYST role.

### Dashboard APIs

- `GET /dashboard/summary` - Get aggregated financial summary showing total income, total expense, net balance, and total record count. Requires ADMIN, ANALYST, or VIEWER role.

### User Management APIs

- `POST /users` - Create a new user with name, email, password, and assigned role. Admin only.
- `GET /users` - Retrieve list of all users in the system with their details. Admin only.
- `PATCH /users/{id}/role` - Change a user's role by ID. Admin only.
- `PATCH /users/{id}/status` - Toggle user status between ACTIVE and INACTIVE by ID. Admin only.
- `DELETE /users/{id}` - Delete a user from the system by ID. Admin only.

## Data Models

### User Entity

Represents a system user with authentication and authorization information.

- `id` - Long, auto-generated primary key
- `name` - String, user's full name
- `email` - String, unique email address used for login
- `password` - String, encrypted password
- `role` - Enum (ADMIN, ANALYST, VIEWER)
- `status` - Enum (ACTIVE, INACTIVE)

### FinancialRecord Entity

Represents a financial transaction or entry in the system.

- `id` - Long, auto-generated primary key
- `amount` - Double, transaction amount
- `type` - Enum (INCOME, EXPENSE)
- `category` - String, record category
- `date` - LocalDate, transaction date
- `description` - String, optional notes
- `createdBy` - User reference, tracks record creator

## Input Validation

The system implements comprehensive validation to ensure data integrity.

### Authentication Validation

Email must follow standard email format. Password must meet minimum strength requirements. Name is required for signup and cannot be empty.

### Record Validation

Amount must be a positive number. Type must be either INCOME or EXPENSE. Category cannot be empty. Date must be a valid date. Description is optional.

### Pagination Validation

Page number cannot be negative. Page size must be greater than zero. Page size cannot exceed 100 records to prevent excessive data transfer.

### User Management Validation

Email must be unique across the system. Role must be one of ADMIN, ANALYST, or VIEWER. Status must be either ACTIVE or INACTIVE.

## Error Handling

The application uses a centralized exception handler that returns consistent error responses across all endpoints.

### Error Response Format

```json
{
    "timestamp": "2026-04-06T10:30:45.123Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Detailed error message",
    "path": "/records"
}
```

### HTTP Status Codes

- 200 OK - Request successful
- 201 Created - Resource created successfully
- 204 No Content - Deletion successful
- 400 Bad Request - Invalid input or validation failure
- 401 Unauthorized - Missing or invalid JWT token
- 403 Forbidden - User lacks required permissions
- 404 Not Found - Resource does not exist
- 409 Conflict - Email already registered or duplicate resource
- 500 Internal Server Error - Unexpected server error

## Security Features

The application implements multiple security layers to protect user data and system integrity.

Passwords are encrypted using BCrypt algorithm with strength factor 10. JWT tokens are signed using HS512 signature algorithm and validated on every request. Tokens expire after 24 hours of issuance. CSRF protection is disabled for API endpoints since the system uses stateless JWT authentication. All endpoints except signup and login require valid authentication. Role-based access control is enforced at the controller level using Spring Security annotations. User account status is checked during login to prevent inactive accounts from accessing the system.

## Configuration

### JWT Settings

Customize JWT behavior in `application.properties`:

```properties
app.jwt.secret=mySecretKeyForJwtTokenGenerationAndValidation123456123456123456123456123456
app.jwt.expiration=86400000
```

The secret key is used to sign and verify tokens. The expiration value is in milliseconds. Default expiration is 24 hours.

### Server Configuration

```properties
server.port=8081
```

### Database Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Design Decisions and Assumptions

### Token Expiration

Tokens expire after 24 hours. Users must login again to obtain a new token. This balances security with user convenience.

### Default Role Assignment

New users registering via signup are automatically assigned the VIEWER role. This follows the principle of least privilege by default.

### User Creation

Only administrators can create users directly with custom roles. Regular users can only sign up as viewers, ensuring controlled access escalation.

### Record Ownership

Financial records track the user who created them through the createdBy field. This provides accountability for all transactions.

### Data Deletion

Records are permanently deleted from the database. Soft delete functionality can be added in future versions for audit trails.

### Pagination Limits

Maximum page size is 100 records to prevent excessive memory consumption and improve API response times.

### Authentication Requirement

Signup and login endpoints are publicly accessible. All other endpoints require valid JWT authentication.

## Future Enhancements

Several improvements can be implemented to enhance the system:

Implement refresh token mechanism to improve security without frequent logins. Add soft delete functionality for audit trail maintenance. Implement rate limiting on authentication endpoints to prevent brute force attacks. Add comprehensive unit and integration tests. Implement audit logging for all critical operations. Add search functionality for financial records. Implement advanced dashboard analytics with trends and forecasting. Add export functionality for records in CSV or PDF format. Implement request logging and monitoring for debugging. Add API documentation using Swagger and OpenAPI standards.

## Troubleshooting

### Database Connection Issues

Verify that PostgreSQL is running and accessible. Check database credentials in application.properties match your PostgreSQL installation. Ensure the finance_db database exists. Check network connectivity if using remote PostgreSQL.

### JWT Token Issues

Verify the token is included in the Authorization header with Bearer prefix. Confirm the token has not expired. Check that the JWT secret in the application matches the secret used to generate the token. Ensure the token is properly formatted without extra whitespace.

### Permission Denied Errors

Verify the user has the required role for the endpoint. Check that the user account status is ACTIVE. Confirm the Bearer token belongs to the correct user. Review the role-based access control configuration.

### Port Already in Use

Change the server.port in application.properties to an available port. Or identify and terminate the process using port 8081. Check firewall settings if port appears blocked.

## Development Guidelines

The codebase follows Spring Boot best practices and clean architecture principles.

Services contain business logic and interact with repositories. Controllers handle HTTP requests and responses without business logic. DTOs transfer data between layers without exposing entities. JPA repositories handle database operations. Security configuration is centralized in SecurityConfig class. Exceptions are handled globally through GlobalExceptionHandler. Entities use Lombok annotations to reduce boilerplate code.

## Project Information

This project is a backend engineering assignment designed to assess skills in API design, data modeling, access control, and business logic implementation. The system demonstrates practical backend development with security, validation, and proper software architecture.

## Support

For issues or questions regarding this backend implementation, review the troubleshooting section or check the configuration settings. Ensure your environment meets all prerequisites before running the application.
