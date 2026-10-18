# Room Management Module

## 1. Overview
The Room Management module manages hospital rooms and provides APIs to create, retrieve, update, list, and soft-delete rooms.

## 2. Technologies
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Jakarta Bean Validation

## 3. Room Entity
The `Room` entity contains the following fields:

- `id` — primary key generated automatically
- `roomNumber` — unique room identifier
- `roomType` — type or category of room
- `floor` — floor where the room is located
- `status` — current room status
- `deleted` — soft-delete flag
- `createdAt` — record creation timestamp
- `updatedAt` — last update timestamp

The entity uses lifecycle callbacks to initialize and update timestamps.

## 4. API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/rooms` | Create a room |
| GET | `/api/v1/rooms/{id}` | Retrieve an active room by ID |
| GET | `/api/v1/rooms?page=0&size=10` | Retrieve active rooms with pagination |
| PUT | `/api/v1/rooms/{id}` | Update a room |
| DELETE | `/api/v1/rooms/{id}` | Soft-delete a room |

## 5. Repository
`RoomRepository` extends `JpaRepository<Room, Long>` and includes methods to:

- Check whether a room number exists among active rooms.
- Find a room by ID while excluding deleted rooms.
- Retrieve paginated active rooms while excluding deleted records.

## 6. Business Rules
- Room numbers must be unique among active rooms.
- Deleted rooms are excluded from normal retrieval operations.
- A room must be active to be retrieved through the standard get-by-ID method.
- Pagination is supported for listing rooms.
- Duplicate room numbers are rejected.
- Missing or deleted room IDs return a not-found response.

## 7. Soft Deletion
Room deletion is logical rather than physical.

When a room is deleted:
- Its deletion flag is set to `true`.
- The database record remains stored.
- Normal room retrieval and listing exclude the deleted room.

## 8. Testing and Verification
The Room Management module was implemented and integrated into the Hospital Management System.

Previously verified behavior includes:
- Room creation and retrieval
- Paginated room listing
- Room update
- Soft deletion
- Exclusion of deleted rooms from normal retrieval

Room ID `1` was soft-deleted, and active room ID `2` (`R-201`) was used for Bed Management testing.

## 9. Main Classes
- Entity: `Room`
- Repository: `RoomRepository`
- Service: `RoomService`
- Service implementation: `RoomServiceImpl`
- Controller: `RoomController`

## 10. Relationship to Bed Management
The Bed Management module references `Room` through a many-to-one relationship.

A bed can only be created or updated against an active room. This helps prevent new bed assignments to rooms that have already been soft-deleted.