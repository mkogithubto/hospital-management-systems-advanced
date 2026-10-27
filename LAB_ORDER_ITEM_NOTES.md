# Lab Order Item Module — Notes

## 1. Overview
The Lab Order Item module connects a laboratory test to a laboratory order. It records the test selected for an order and preserves the test price at the time of ordering.

## 2. Entity: `LabOrderItem`

**File:** `entity/LabOrderItem.java`

Main fields:
- `id` — Primary key
- `labOrder` — Many-to-one relationship with `LabOrder`
- `labTest` — Many-to-one relationship with `LabTest`
- `priceAtOrder` — Test price captured when the item is created
- `status` — Defaults to `ORDERED`
- `deleted` — Soft-delete flag mapped to `is_deleted`
- `createdAt` — Creation timestamp
- `updatedAt` — Last-update timestamp

### Relationships
- Many `LabOrderItem` records can belong to one `LabOrder`.
- Many `LabOrderItem` records can reference one `LabTest`.

## 3. API Endpoints

Base URL: `/api/v1/lab-order-items`

| Method | Endpoint | Purpose | Success |
|---|---|---|---|
| POST | `/api/v1/lab-order-items` | Add a test to a lab order | 201 Created |
| GET | `/api/v1/lab-order-items/{id}` | Retrieve an item by ID | 200 OK |
| GET | `/api/v1/lab-order-items/by-order/{labOrderId}?page=0&size=10` | List items for an order with pagination | 200 OK |
| DELETE | `/api/v1/lab-order-items/{id}` | Soft-delete an item | 204 No Content |

An update API is not included because it is not currently required by the workflow.

## 4. Request DTO

**Class:** `LabOrderItemRequest`

```json
{
  "labOrderId": 2,
  "labTestId": 2
}
```

Both fields are mandatory and validated using `@NotNull`.

## 5. Response DTO

**Class:** `LabOrderItemResponse`

Returns:
- Item ID
- Lab order ID and order code
- Lab test ID, test code, and test name
- Price at order
- Status
- Creation and update timestamps

## 6. Business Logic

- The referenced lab order must exist and must not be soft-deleted.
- The referenced lab test must exist and must not be soft-deleted.
- The same active lab test cannot be added more than once to the same lab order.
- `priceAtOrder` is copied from the lab test's current price when the item is created.
- Items are retrieved only when they are not soft-deleted.
- Deletion sets `is_deleted = 1`; the database record is retained.

## 7. Repository

**Class:** `LabOrderItemRepository`

Key methods:
- `findByIdAndDeletedFalse(id)`
- `findAllByLabOrderIdAndDeletedFalse(labOrderId, pageable)`
- `existsByLabOrderIdAndLabTestIdAndDeletedFalse(labOrderId, labTestId)`

## 8. Manual Testing Results

All tested scenarios passed:

- Created a lab order item successfully (`201 Created`).
- Retrieved an item by ID (`200 OK`).
- Retrieved paginated items for a lab order (`200 OK`).
- Rejected duplicate items with the expected conflict message (`409 Conflict`).
- Soft-deleted an item (`204 No Content`).
- Confirmed the deleted item returns not found.
- Confirmed the deleted item is excluded from the order listing.
- Verified in MySQL that the record remains with `is_deleted = 1`.

### Test Record
- Lab Order Item ID: `1`
- Lab Order ID: `2`
- Lab Test ID: `