# Pharmacy Management — Medicine Batch Module Notes

## 1. Overview

The Medicine Batch module manages individual stock batches associated with medicines in the Hospital Management System (HMS).

A medicine master record stores general information about a medicine, while each batch stores its specific batch number, manufacturing date, expiry date, received quantity, available quantity, and purchase price.

**Technology stack:** Java 21, Spring Boot, Spring Data JPA, Hibernate, MySQL, Maven.

**Git branch:** `feature/pharmacy-management`

## 2. Entity Relationship

**Medicine (1) → (Many) MedicineBatch**

The `MedicineBatch` entity contains a `@ManyToOne` relationship with `Medicine`.

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "medicine_id", nullable = false)
private Medicine medicine;
```

- `medicines` stores medicine master information.
- `medicine_batches` stores individual batch records.
- `medicine_id` is the foreign key referencing the associated medicine.
- A batch cannot be created for a missing or soft-deleted medicine.

The relationship is defined on the `MedicineBatch` side; a corresponding `@OneToMany` field is not required in `Medicine`.

## 3. Entity Fields

The `MedicineBatch` entity contains:

| Field | Purpose |
|---|---|
| `id` | Auto-generated primary key |
| `medicine` | Associated medicine entity |
| `batchNumber` | Batch identification number |
| `manufacturingDate` | Manufacturing date; optional |
| `expiryDate` | Expiry date; required |
| `quantityReceived` | Original quantity received |
| `availableQuantity` | Current available stock |
| `purchasePrice` | Purchase price for the batch |
| `deleted` | Soft-delete flag |
| `createdAt` | Creation timestamp |
| `updatedAt` | Last-update timestamp |

The entity uses `@PrePersist` and `@PreUpdate` lifecycle callbacks to manage audit timestamps.

### Database Constraints

The table has a unique constraint on:

```text
(medicine_id, batch_number)
```

This prevents the same batch number from being reused for the same medicine at the database level, including when an earlier matching batch has been soft-deleted.

## 4. Repository

File: `repository/MedicineBatchRepository.java`

The repository extends `JpaRepository<MedicineBatch, Long>`.

| Method | Purpose |
|---|---|
| `findByIdAndDeletedFalse(id)` | Retrieves an active batch by ID |
| `existsByMedicineIdAndBatchNumberAndDeletedFalse(medicineId, batchNumber)` | Checks for an existing active batch number for a medicine |
| `findAllByDeletedFalse(pageable)` | Retrieves active batches with pagination |

Spring Data JPA derives the queries from the method names.

## 5. DTOs

### MedicineBatchRequest

Used when creating a batch.

Fields:
- `medicineId`
- `batchNumber`
- `manufacturingDate`
- `expiryDate`
- `quantityReceived`
- `purchasePrice`

Validations include:
- Medicine ID must be greater than zero.
- Batch number is required and limited to 50 characters.
- Expiry date is required.
- Quantity received must be at least 1.
- Purchase price cannot be negative.

### MedicineBatchUpdateRequest

Used when updating a batch.

Editable fields:
- `batchNumber`
- `manufacturingDate`
- `expiryDate`
- `purchasePrice`

Stock quantities and the medicine relationship cannot be changed through this update request.

### MedicineBatchResponse

Returns:
- Batch ID and batch number
- Medicine ID, medicine code, and medicine name
- Manufacturing and expiry dates
- Quantity received and available quantity
- Purchase price
- Creation and update timestamps

The response does not expose the internal soft-delete flag.

## 6. Service Layer

Files:
- `service/MedicineBatchService.java`
- `service/impl/MedicineBatchServiceImpl.java`

The service interface defines five operations.

### Create Medicine Batch

`createMedicineBatch(request)`

1. Finds the associated medicine using `findByIdAndDeletedFalse`.
2. Rejects a missing or soft-deleted medicine.
3. Checks for a duplicate active batch number for that medicine.
4. Validates the expiry date against the manufacturing date, when supplied, and the current date.
5. Sets `availableQuantity` equal to `quantityReceived`.
6. Saves the batch with `deleted = false`.
7. Maps the saved entity to `MedicineBatchResponse`.

### Get Medicine Batch by ID

`getMedicineBatchById(id)`

- Retrieves only non-deleted batches.
- Throws `ResourceNotFoundException` if the batch is missing or soft-deleted.
- Returns `MedicineBatchResponse` for a valid batch.

### Get All Medicine Batches

`getAllMedicineBatches(page, size)`

- Uses `PageRequest.of(page, size)`.
- Retrieves only non-deleted batches.
- Maps entities to response DTOs.
- Returns Spring Data's paginated `Page<MedicineBatchResponse>`.

### Update Medicine Batch

`updateMedicineBatch(id, request)`

- Retrieves the active batch.
- Validates the expiry and manufacturing dates.
- Checks for duplicate batch numbers when the batch number changes.
- Updates the batch number, manufacturing date, expiry date, and purchase price.
- Preserves `quantityReceived` and `availableQuantity`.
- Saves and returns the updated response.

### Soft Delete Medicine Batch

`deleteMedicineBatch(id)`

- Retrieves the active batch.
- Sets `deleted = true`.
- Saves the entity instead of physically deleting the database row.
- Throws `ResourceNotFoundException` if the batch is missing or already soft-deleted.

## 7. REST API Endpoints

Base URL:

```text
http://localhost:8080/api/v1/medicine-batches
```

| Method | Endpoint | Purpose | Expected status |
|---|---|---|---|
| POST | `/api/v1/medicine-batches` | Create a batch | `201 Created` |
| GET | `/api/v1/medicine-batches/{id}` | Get batch by ID | `200 OK` |
| GET | `/api/v1/medicine-batches` | List batches | `200 OK` |
| GET | `/api/v1/medicine-batches?page=0&size=10` | Paginated listing | `200 OK` |
| PUT | `/api/v1/medicine-batches/{id}` | Update batch details | `200 OK` |
| DELETE | `/api/v1/medicine-batches/{id}` | Soft-delete a batch | `204 No Content` |

Missing, nonexistent, or soft-deleted batches return `404 Not Found` for ID-based retrieval and deletion.

Duplicate active batch numbers return `409 Conflict`.

## 8. Postman Test Results

### Medicine Master prerequisite

An active medicine was created successfully:

```json
{
  "id": 2,
  "medicineCode": "MED-0002",
  "name": "Amoxicillin",
  "manufacturer": "XYZ Pharma",
  "status": "ACTIVE"
}
```

### Batch creation

Request:

```http
POST /api/v1/medicine-batches
```

```json
{
  "medicineId": 2,
  "batchNumber": "AMX-BATCH-001",
  "manufacturingDate": "2026-08-01",
  "expiryDate": "2028-08-01",
  "quantityReceived": 100,
  "purchasePrice": 25.50
}
```

Result: `201 Created`.

The API returned batch ID `1`, associated medicine ID `2`, and `availableQuantity = 100`.

### Duplicate batch test

Creating the same batch again for medicine ID `2` returned:

```text
409 Conflict
Batch AMX-BATCH-001 already exists for this medicine
```

Result: Passed.

### Get by ID

- `/api/v1/medicine-batches/1` returned the expected batch with `200 OK`.
- `/api/v1/medicine-batches/199` returned `404 Not Found`.

Result: Passed.

### Pagination

Both requests returned the expected paginated response:

```http
GET /api/v1/medicine-batches
GET /api/v1/medicine-batches?page=0&size=10
```

Before deletion, the response contained one batch:

- `totalElements = 1`
- `totalPages = 1`
- `number = 0`
- `size = 10`

Result: Passed.

### Update

The batch was updated with:

- Expiry date: `2028-12-31`
- Purchase price: `27.50`

The response retained `quantityReceived = 100` and `availableQuantity = 100`.

Result: The updated field values were returned correctly. The observed `updatedAt` timestamp appeared unchanged from creation, so verify the timestamp behavior in a future update test.

### Soft deletion

Request:

```http
DELETE /api/v1/medicine-batches/1
```

Result: `204 No Content`.

Follow-up checks:

- `GET /api/v1/medicine-batches/1` returned `404 Not Found`.
- `GET /api/v1/medicine-batches` returned `content: []` and `totalElements: 0`.

Result: Soft-delete behavior verified.

## 9. Build Verification

Command:

```powershell
.\mvnw.cmd clean test
```

**Result: BUILD SUCCESS**

The configured Maven test suite passed. Postman API checks were performed manually; the successful build alone does not mean that automated tests cover every MedicineBatch endpoint.

## 10. Important Business Rules and Future Work

1. Never physically delete medicine batch records; use soft deletion.
2. Normal retrieval and listing must exclude soft-deleted records.
3. Create batches only for active medicines.
4. Keep `quantityReceived` as the original received quantity.
5. Do not allow ordinary batch updates to overwrite inventory quantities.
6. Future stock deductions and adjustments should be implemented through dedicated inventory operations.
7. The database unique constraint prevents reuse of a batch number for the same medicine after soft deletion.
8. Review the update timestamp behavior.
9. Consider explicit validation and error handling for negative pagination values and invalid date rules.

## 11. Next Development Phase

Potential next steps for Pharmacy Management:

- Stock adjustment and stock movement history.
- Low-stock and expiry alerts.
- Medicine dispensing or sales workflows.
- Automated service and controller tests.
- Inventory reporting.

**Current status:** Medicine Master and MedicineBatch CRUD operations have been implemented and manually tested, with soft deletion and pagination verified. The next feature should build on these foundations without bypassing inventory quantity controls.
