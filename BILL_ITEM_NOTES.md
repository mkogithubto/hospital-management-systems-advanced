# Billing Module — BillItem Component Notes

## 1. Overview

The BillItem component manages individual charges associated with a bill. Each item belongs to a parent Bill and stores its name, description, quantity, unit price, and calculated amount.

## 2. Package Structure

| Package | Class |
|---|---|
| `entity` | `BillItem.java` |
| `repository` | `BillItemRepository.java` |
| `dto.request` | `BillItemRequest.java` |
| `dto.response` | `BillItemResponse.java` |
| `service` | `BillItemService.java` |
| `service.impl` | `BillItemServiceImpl.java` |
| `controller` | `BillItemController.java` |

## 3. API Endpoints

Base URL: `http://localhost:8080/api/v1/bill-items`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/bill-items` | Create a bill item |
| GET | `/api/v1/bill-items/{id}` | Retrieve an active bill item |
| GET | `/api/v1/bill-items/bill/{billId}?page=0&size=10` | List active items for a bill |
| DELETE | `/api/v1/bill-items/{id}` | Soft-delete a bill item |

## 4. Request Fields and Validation

- `billId` — required; identifies the parent bill.
- `itemName` — required; maximum 150 characters.
- `description` — optional; maximum 500 characters.
- `quantity` — required; must be at least 1.
- `unitPrice` — required; must be greater than zero.

The client does not supply `totalAmount`. The backend calculates it.

## 5. Amount Calculation

`totalAmount = quantity × unitPrice`

Example:

- Quantity: 1
- Unit price: ₹500.00
- Calculated total: ₹500.00

Amounts use `BigDecimal` with precision 12 and scale 2.

## 6. Business Rules

- A bill item must reference an existing, active bill.
- Quantity must be positive.
- Unit price must be greater than zero.
- Bill items are retrieved only when active.
- Bill-item listing is paginated and filters out deleted items.
- Deleting a bill item sets `is_deleted = 1`; it does not physically delete the row.

## 7. Testing Completed

Verified using Postman and MySQL:

- Successful item creation — `201 Created`.
- Get BillItem by ID — successful.
- Paginated items by bill ID — successful.
- Quantity `0` rejected by validation.
- Unit price `0` rejected by validation.
- Nonexistent parent bill rejected with `404 Not Found`.
- Soft deletion returned `204 No Content`.
- Retrieving the deleted item returned `404 Not Found`.
- Deleted item excluded from the paginated list.
- MySQL confirmed the item remains in `bill_items` with `is_deleted = 1`.

## 8. Current Scope and Future Work

BillItem creation currently calculates each item's amount independently. It does not automatically synchronize the parent Bill's subtotal, total amount, or balance.

Bill-level recalculation and integration with payment processing should be addressed as part of the broader Billing workflow.

## 9. Build Verification

The following command completed successfully after implementation:


```powershell
.\mvnw.cmd clean compile
```


## Bill–BillItem Integration

- Creating a BillItem increases the parent bill's subtotal and recalculates its total, balance, and payment status.
- Deleting a BillItem decreases the subtotal and recalculates the bill's financial values.
- The bill's discount and tax are included when recalculating the total.
- A BillItem cannot be deleted if the resulting bill total would be less than the amount already paid. The API returns **409 Conflict**.
- BillItems use soft deletion. Deleted items remain in the database with `is_deleted = 1` but are excluded from normal retrieval and listing.
- Parent bill updates and BillItem operations run transactionally and use a pessimistic lock on the bill to protect financial consistency.

### Verified Tests

- Item creation updates parent bill totals.
- Item deletion restores the appropriate bill totals.
- Deleted items are excluded from retrieval and listing.
- Deletion is rejected when it would make the bill total lower than the amount already paid.
- MySQL verification confirmed soft-deleted items have `is_deleted = 1` and active items have `is_deleted = 0`.