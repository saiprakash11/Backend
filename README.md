# EMS Unified Backend

Single Spring Boot application that consolidates all four original microservices into **one process on port 8080**. The Spring Cloud Gateway is eliminated entirely.

## Architecture Change

| Before (5 processes) | After (1 process) |
|---|---|
| gateway → :8080 | **ems-unified → :8080** |
| login-service → :8081 | _(merged)_ |
| employee-service → :8085 | _(merged)_ |
| hr-service → :8089 | _(merged)_ |
| management-service → :8092 | _(merged)_ |
| frontend dev server → :3000 | frontend dev server → :3000 ✓ |

All API routes and paths are **100% identical** — no frontend changes required.

## Structure

```
ems-unified/
├── pom.xml
└── src/main/java/com/ems/
    ├── EmsApplication.java     ← @SpringBootApplication entry point
    ├── config/
    │   ├── SecurityConfig.java ← JWT filter, CORS, permit rules
    │   └── OpenApiConfig.java  ← Swagger/OpenAPI setup
    ├── security/
    │   ├── JwtUtil.java
    │   └── JwtAuthFilter.java
    ├── auth/                   ← From login-service (:8081)
    │   ├── controller/AuthController.java
    │   ├── service/AuthService.java
    │   ├── entity/User.java
    │   ├── repository/UserRepository.java
    │   └── dto/LoginRequest, LoginResponse, ChangePasswordRequest
    ├── employee/               ← From employee-service (:8085)
    │   ├── controller/EmployeeDashboardApiController.java
    │   ├── controller/EmployeeProfileController.java
    │   ├── entity/EmployeeProfile.java
    │   ├── repository/EmployeeProfileRepository.java
    │   └── service/EmployeeProfileService.java
    ├── hr/                     ← From hr-service (:8089)
    │   ├── attendance/
    │   ├── common/             (Employee entity, EmployeeStore)
    │   ├── dashboard/
    │   ├── directory/
    │   ├── leave/
    │   ├── onboarding/
    │   ├── payroll/
    │   ├── recruitment/
    │   ├── reports/
    │   └── userstatus/         (UserStatusController uses auth.entity.User)
    └── management/             ← From management-service (:8092)
        ├── approvals/
        ├── meetings/
        ├── performance_reviews/
        ├── projects/
        └── exception/
```

## Prerequisites

- Java 21
- Maven 3.8+
- MySQL running with the `emp` database loaded from `ems_enterprise_schema.sql`
- Python 3.x (for the frontend dev server)

## Quick Start

### 1. Load the Database

```sql
mysql -u root -p < ems_enterprise_schema.sql
```

### 2. Start the Backend (port 8080)

```bash
cd ems-unified
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn package -DskipTests
java -jar target/ems-unified-1.0.0.jar
```

### 3. Start the Frontend Dev Server (port 3000)

```bash
# From the frontend/ directory
python ems_dev_server.py
```

Open: **http://localhost:3000/authentication/unified_login_portal/code.html**

### 4. Swagger UI

**http://localhost:8080/swagger-ui.html**

## Configuration

Edit `src/main/resources/application.properties`:

| Property | Default | Notes |
|---|---|---|
| `server.port` | `8080` | Backend port |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/emp` | MySQL connection |
| `spring.datasource.username` | `root` | |
| `spring.datasource.password` | `root` | Change in production |
| `app.jwt.secret` | *(see file)* | **Change in production!** |
| `app.jwt.expiration-ms` | `86400000` | 24 hours |

## API Routes (unchanged from gateway)

### Auth
| Method | Path | Public |
|---|---|---|
| POST | `/api/auth/login` | ✓ |
| POST | `/api/auth/logout` | |
| POST | `/api/auth/change-password` | |
| GET | `/api/auth/me` | |

### Employee
| Method | Path |
|---|---|
| GET | `/api/employees/{code}/dashboard` |
| GET/POST | `/api/employees/{code}/attendance` |
| POST | `/api/employees/{code}/attendance/check-in` |
| POST | `/api/employees/{code}/attendance/check-out` |
| GET/POST | `/api/employees/{code}/leave` |
| GET | `/api/employees/{code}/performance` |
| GET/PUT | `/api/employees/{code}/profile` |
| GET | `/api/employees/{code}/notifications` |
| GET/POST | `/api/employee-profiles/{code}` |

### HR
| Method | Path |
|---|---|
| GET | `/api/hr/employees` |
| GET | `/api/dashboard/summary` |
| GET | `/api/reports/attendance` |
| GET | `/api/reports/leave` |
| GET/POST | `/api/attendance` |
| GET/POST | `/api/leave` |
| GET | `/api/payroll` |
| GET | `/api/recruitment` |
| GET | `/api/onboarding/tracking` |
| GET | `/api/users` |
| PUT | `/api/users/{code}/activate` |
| PUT | `/api/users/{code}/deactivate` |

### Management
| Method | Path |
|---|---|
| GET/POST | `/api/projects` |
| PUT/DELETE | `/api/projects/{id}` |
| GET/POST | `/api/meetings` |
| GET/POST | `/api/approvals` |
| PUT | `/api/approvals/{id}/approve` |
| PUT | `/api/approvals/{id}/reject` |
| GET/POST | `/api/performance-reviews` |

## Frontend Session Keys (unchanged)

| Key | Value |
|---|---|
| `ems_token` | JWT |
| `ems_role` | ADMIN / HR / MANAGEMENT / EMPLOYEE |
| `ems_employeeCode` | Employee identifier |
| `ems_email` | Login email |
| `ems_fullName` | Display name |

## Notes

- **No gateway needed** — all routing is handled internally within the single Spring Boot context.
- **No port conflicts** — only one process needs port 8080.
- **JWT security** is enforced at the application level (`SecurityConfig`) rather than at a gateway.
- **Database unchanged** — same `emp` MySQL schema, all tables shared.
- **File uploads** are served from `file:uploads/` in the working directory (same as before).
