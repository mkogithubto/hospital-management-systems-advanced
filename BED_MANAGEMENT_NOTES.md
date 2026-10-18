# Bed Management Module

## 1. Overview
The Bed Management module manages hospital beds and their association with rooms. It supports creating, retrieving, updating, and soft-deleting beds.

## 2. Technologies
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Jakarta Bean Validation

## 3. API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/beds` | Create a bed |
| GET | `/api/v1/beds/{id}` | Retrieve an active bed |
| GET | `/api/v1/beds?page=0&size=10` | Retrieve active beds with pagination |
| PUT | `/api/v1/beds/{id}` | Update a bed |
| DELETE | `/api/v1/beds/{id}` | Soft-delete a bed |

## 4. Entity and Relationships
- `Bed` is associated with `Room` through a many-to-one relationship.
- Each bed stores its bed number, room, bed type, status, deletion flag, creation timestamp, and update timestamp.
- New beds receive the status `AVAILABLE`.

## 5. Validation and Business Rules
- Bed number and bed type are required.
- Room ID is required and must be positive.
- Bed numbers must be unique within a room, including soft-deleted records.
- A bed can only be assigned to an active room.
- Duplicate bed numbers return HTTP 409 Conflict.
- Invalid request fields return HTTP 400 Bad Request.

## 6. Soft Deletion
- Deleting a bed sets `is_deleted = true`.
- The database record is retained.
- Normal get-by-ID and paginated listing operations exclude deleted beds.
- A deleted bed number cannot be reused in the same room because the database uniqueness constraint includes all records.

## 7. Timestamps
- `@PrePersist` initializes `createdAt` and `updatedAt`.
- `@PreUpdate` updates `updatedAt`.
- `saveAndFlush()` ensures the update is flushed before mapping the response.

## 8. Testing
Verified through Postman and MySQL:
- Successful creation and update
- Duplicate bed rejection
- Pagination and active-bed retrieval
- Request validation
- Soft deletion and retrieval of deleted records
- Timestamp updates

## 9. Main Classes
- Entity: `Bed`
- Request DTO: `BedRequest`
- Response DTO: `BedResponse`
- Repository: `BedRepository`
- Service interface: `BedService`
- Service implementation: `BedServiceImpl`
- Controller: `BedController`
- Exception handling: `GlobalExceptionHandler`