# JWT Authentication Implementation

This document explains the JWT (JSON Web Token) authentication system implemented for the Software Project Management Backend.

## Overview

JWT authentication has been implemented for both **ProjectManager** and **TeamMember** entities. The system provides secure token-based authentication with role-based access control.

## Components

### 1. Security Classes (in `security/` package)

- **JwtProvider**: Generates and validates JWT tokens
  - `generateToken(Authentication)`: Generate token from Spring Authentication
  - `generateTokenFromId(userId, username, userType)`: Generate token directly from user details
  - `validateToken(token)`: Validate token signature and expiration
  - `getUserIdFromToken(token)`: Extract user ID from token
  - `getUserTypeFromToken(token)`: Extract user type (MANAGER or TEAM_MEMBER) from token
  - `isTokenExpired(token)`: Check if token has expired

- **CustomUserPrincipal**: Custom UserDetails implementation
  - Stores user ID, username, password, userType, and enabled status
  - Implements Spring Security UserDetails interface
  - Returns authorities based on userType (ROLE_MANAGER or ROLE_TEAM_MEMBER)

- **CustomUserDetailsService**: UserDetailsService implementation
  - `loadUserByUsername(username)`: Load user from either ProjectManager or TeamMember table
  - `loadUserById(userId, userType)`: Load user by ID and type
  - Supports both MANAGER and TEAM_MEMBER user types

- **JwtAuthenticationFilter**: OncePerRequestFilter for JWT validation
  - Extracts JWT token from "Authorization: Bearer {token}" header
  - Validates token and sets Spring Security authentication in context
  - Applied to all endpoints except `/api/auth/**` and user creation endpoints

- **SecurityConfig**: Spring Security configuration
  - Configures HttpSecurity with stateless session management
  - Enables method-level security with @PreAuthorize annotations
  - Configures BCrypt password encoding
  - Adds JwtAuthenticationFilter to filter chain

### 2. DTOs (in `dto/` package)

- **LoginRequest**: Request body for login endpoints
  ```json
  {
    "username": "john_doe",
    "password": "password123",
    "userType": "MANAGER"  // or "TEAM_MEMBER"
  }
  ```

- **AuthResponse**: Response body for successful authentication
  ```json
  {
    "token": "eyJhbGc...",
    "type": "Bearer",
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "userType": "MANAGER",
    "message": "User authenticated successfully"
  }
  ```

### 3. Controllers

- **AuthController** (new): Handles authentication
  - `POST /api/auth/login`: General login endpoint (attempts to find user in both tables)
  - `POST /api/auth/login/manager`: ProjectManager-specific login
  - `POST /api/auth/login/team-member`: TeamMember-specific login

- **ProjectManagerController**: Updated with @PreAuthorize annotations
  - `POST /api/projectmanagers/create`: Public (no auth required)
  - `GET /api/projectmanagers/get/{id}`: Requires MANAGER role
  - `PUT /api/projectmanagers/update/{id}`: Requires MANAGER role
  - `DELETE /api/projectmanagers/delete/{id}`: Requires MANAGER role

- **TeamMemberController**: Updated with @PreAuthorize annotations
  - `POST /api/teammembers/create`: Public (no auth required)
  - `GET /api/teammembers/get/{id}`: Requires TEAM_MEMBER role
  - `PUT /api/teammembers/update/{id}`: Requires TEAM_MEMBER role
  - `DELETE /api/teammembers/delete/{id}`: Requires TEAM_MEMBER role

### 4. Service Updates

- **ProjectManagerServiceImpl**: Now encodes passwords using BCryptPasswordEncoder
- **TeamMemberServiceImpl**: Now encodes passwords using BCryptPasswordEncoder

### 5. Repository Updates

- **ProjectManagerRepository**: Added `findByUsername(String username)` method
- **TeamMemberRepository**: Added `findByUsername(String username)` method

## Configuration

Add the following properties to `application.properties`:

```properties
# JWT Configuration
app.jwtSecret=mySecretKeyForJWTTokenGenerationAndValidationPurposeThatIsLongEnough
app.jwtExpirationMs=86400000  # 24 hours in milliseconds
```

**Note**: Change `app.jwtSecret` to a strong, random string for production.

## Dependencies Added to pom.xml

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Usage Flow

### 1. User Registration (Create New User)

**ProjectManager Registration:**
```bash
POST /api/projectmanagers/create
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com"
}
```

**TeamMember Registration:**
```bash
POST /api/teammembers/create
Content-Type: application/json

{
  "username": "jane_smith",
  "password": "password456",
  "email": "jane@example.com",
  "availability": true
}
```

**Response:**
```json
{
  "userID": 1,
  "username": "john_doe",
  "password": "$2a$10$...",  // Encoded password
  "email": "john@example.com"
}
```

### 2. User Login

**General Login (finds user in either table):**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**ProjectManager-Specific Login:**
```bash
POST /api/auth/login/manager
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**TeamMember-Specific Login:**
```bash
POST /api/auth/login/team-member
Content-Type: application/json

{
  "username": "jane_smith",
  "password": "password456"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJqb2huX2RvZSIsInVzZXJUeXBlIjoiTUFOQUdFUiIsImlhdCI6MTcxNjIzOTAyMiwiZXhwIjoxNzE2MzI1NDIyfQ...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "userType": "MANAGER",
  "message": "User authenticated successfully"
}
```

**Failure Response (401 Unauthorized):**
```json
{
  "message": "Invalid username or password"
}
```

### 3. Access Protected Endpoints

Include the JWT token in the `Authorization` header with "Bearer " prefix:

```bash
GET /api/projectmanagers/get/1
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwi...
```

**Success Response (200 OK):**
```json
{
  "userID": 1,
  "username": "john_doe",
  "password": "$2a$10$...",
  "email": "john@example.com"
}
```

**Failure Response (403 Forbidden - Missing Role):**
```
Access Denied
```

**Failure Response (401 Unauthorized - Invalid Token):**
```
Unauthorized
```

## Token Structure

JWT tokens contain three parts separated by dots (`.`):
1. **Header**: Contains token type and hashing algorithm
2. **Payload**: Contains claims (subject, username, userType, iat, exp)
3. **Signature**: Generated using HS512 algorithm with secret key

Example decoded payload:
```json
{
  "sub": "1",
  "username": "john_doe",
  "userType": "MANAGER",
  "iat": 1716239022,
  "exp": 1716325422
}
```

- `sub`: User ID
- `username`: Username
- `userType`: User type (MANAGER or TEAM_MEMBER)
- `iat`: Issued at (timestamp)
- `exp`: Expiration time (timestamp)

## Security Features

1. **Password Encoding**: All passwords are encoded using BCrypt before storing in database
2. **Token Validation**: Tokens are validated on each request
3. **Token Expiration**: Tokens expire after 24 hours (configurable)
4. **Role-Based Access Control**: Endpoints require specific roles
5. **Stateless Authentication**: Uses JWT tokens instead of sessions
6. **CSRF Protection**: Disabled for API (not needed with JWT)

## Testing with cURL

### Register a new Project Manager
```bash
curl -X POST http://localhost:8080/api/projectmanagers/create \
  -H "Content-Type: application/json" \
  -d '{"username":"manager1","password":"pass123","email":"manager@example.com"}'
```

### Login and get token
```bash
curl -X POST http://localhost:8080/api/auth/login/manager \
  -H "Content-Type: application/json" \
  -d '{"username":"manager1","password":"pass123"}' | jq '.token'
```

### Access protected endpoint with token
```bash
curl -X GET http://localhost:8080/api/projectmanagers/get/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Troubleshooting

### Invalid Token Error
- Verify token hasn't expired
- Ensure token is complete (all three parts with dots)
- Check that "Bearer " prefix is included in Authorization header

### Access Denied Error
- Verify user has correct role for endpoint
- Check token contains correct userType
- Ensure user is logged in as correct user type

### User Not Found
- Verify username/password is correct
- Ensure user exists in correct table (ProjectManager or TeamMember)
- Check database connection

## Future Enhancements

1. Token refresh mechanism
2. Token blacklisting for logout
3. Role-based authorization for specific resources
4. API key authentication for service-to-service communication
5. OAuth2 integration for third-party authentication
6. Two-factor authentication (2FA)
7. Rate limiting for login attempts
