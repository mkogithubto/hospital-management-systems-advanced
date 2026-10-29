# Billing Module — Bill Component Notes

## 1. Overview

The Bill component manages patient billing records in the Hospital Management System using Spring Boot, Spring Data JPA, Hibernate, and MySQL.

**Implemented features:**
- Create a bill linked to a patient and an OPD visit or IPD admission.
- Retrieve a bill by ID.
- Retrieve bills using pagination.
- Validate bill details and encounter ownership.
- Calculate total, paid, and outstanding amounts.
- Soft-delete bills.

## 2. Package Structure

| Package | Class |
|---|---|
| `entity` | `Bill.java` |
| `repository` | `BillRepository.java` |
| `dto.request` | `BillRequest.java` |
| `dto.response` | `BillResponse.java` |
| `service` | `BillService.java` |
| `service.impl` | `BillServiceImpl.java` |
| `controller` | `BillController.java` |

## 3. Bill Entity

Important fields:

- `id` — primary key.
- `billNumber` — unique bill number.
- `patient` — mandatory relationship with Patient.
- `opdVisit` — optional relationship with OPD Visit.
- `ipdAdmission` — optional relationship with IPD Admission.
- `billDate` — date and time of billing.
- `subtotal`, `discountAmount`, `taxAmount` — financial inputs.
- `totalAmount` — calculated bill total.
- `paidAmount` — amount paid, initially zero.
- `balanceAmount` — outstanding amount.
- `paymentStatus` — initially `UNPAID`, or `PAID` for a zero-total bill.
- `deleted` — soft-delete flag mapped to `is_deleted`.
- `createdAt`, `updatedAt` — audit timestamps.

Financial amounts use `BigDecimal` with precision 12 and scale 2.

## 4. API Endpoints

Base URL: `http://localhost:8080/api/v1/bills`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/bills` | Create a bill |
| GET | `/api/v1/bills/{id}` | Retrieve an active bill |
| GET | `/api/v1/bills?page=0&size=10` | Retrieve active bills with pagination |
| DELETE | `/api/v1/bills/{id}` | Soft-delete a bill |

There is currently no update-bill endpoint.

## 5. Bill Creation Request

Example:

```json
{
  "billNumber": "BILL-000010",
  "patientId": 1,
  "opdVisitId": 1,
  "subtotal": 2500.00,
  "discountAmount": 100.00,
  "taxAmount": 0.00
}
```

The request accepts the patient ID and either an OPD visit ID or an IPD admission ID. The client does not supply `totalAmount`, `paidAmount`, `balanceAmount`, or `paymentStatus`; these are controlled by the backend.

## 6. Financial Calculation

**Formula:**

`totalAmount = subtotal - discountAmount + taxAmount`

`paidAmount = 0`

`balanceAmount = totalAmount - paidAmount`

Example:

- Subtotal: ₹2,500
- Discount: ₹100
- Tax: ₹0
- Total: ₹2,400
- Paid: ₹0
- Balance: ₹2,400
- Status: `UNPAID`

A zero-total bill is marked `PAID` by the current service logic.

## 7. Validation Rules

- Bill number is required and must not duplicate an existing active bill number.
- Patient ID is required.
- Exactly one encounter must be supplied: OPD visit or IPD admission.
- The selected encounter must exist and be active.
- The encounter must belong to the specified patient.
- Subtotal, discount, and tax cannot be negative.
- Discount cannot exceed subtotal.
- Invalid request fields are rejected through Jakarta Bean Validation.

Typical responses:
- `201 Created` — bill created.
- `200 OK` — bill retrieved.
- `204 No Content` — bill soft-deleted.
- `400 Bad Request` — request validation failure.
- `404 Not Found` — bill or encounter not found.
- `409 Conflict` — duplicate bill number or business-rule violation.

## 8. Soft Deletion

Bills must not be physically deleted.

The delete operation sets `is_deleted = 1`. Normal retrieval and listing use repository methods that exclude deleted bills:

- `findByIdAndDeletedFalse`
- `findByBillNumberAndDeletedFalse`
- `findAllByDeletedFalse`

**Important:** The database has a unique constraint on `billNumber`. Therefore, a bill number may remain unavailable for reuse even after its bill is soft-deleted.

## 9. Testing Completed

The following behaviors were verified using Postman:

- Bill creation and financial calculations.
- Get bill by ID.
- Paginated bill listing.
- Duplicate bill number rejection.
- Rejection when both encounter IDs are supplied.
- Rejection when neither encounter ID is supplied.
- Patient–OPD visit mismatch rejection.
- Patient–IPD admission mismatch rejection.
- Discount greater than subtotal rejection.
- Negative subtotal rejection.
- Missing patient ID and bill number validation.
- Zero-total bill marked `PAID`.
- Invalid OPD visit ID returns `404 Not Found`.
- Soft deletion returns `204 No Content`.
- Deleted bill retrieval returns `404 Not Found`.
- Deleted bill is excluded from the paginated list.
- MySQL confirms the deleted record remains present with `is_deleted = 1`.

## 10. Build Verification

After implementing the Bill component, the following command completed successfully:

```powershell
.\mvnw.cmd clean compile
```

## 11. Future Components

The following Billing components remain separate implementation tasks:

1. **BillItem** — individual bill line items and their amounts.
2. **Payment** — payment transactions and payment tracking.

These components should be implemented and tested step by step. All new components must follow the project's soft-delete convention and have concise notes when completed.


## BillItem Integration

BillItem creation and deletion automatically recalculate the parent bill's subtotal, total amount, balance, and payment status. Discount and tax amounts are included in the calculation. Deletion is rejected with **409 Conflict** if the resulting bill total would be less than the amount already paid. Operations are transactional, the parent bill is locked during financial updates, and BillItems use soft deletion.

**Verified:** Bill totals remain unchanged after a rejected deletion; active items remain retrievable, and soft-deleted items are excluded from normal API reads and lists.


Billing Module — Testing Notes

1. Payment Creation and Bill Balance Update

Endpoint: POST /api/v1/payments

Test: Created a payment of ₹1,100 for Bill ID 5 using payment reference PAY-000005.

Result: Passed — payment was created successfully.

Before payment:

Total amount: ₹2,100

Paid amount: ₹1,000

Balance amount: ₹1,100

Status: PARTIALLY_PAID

After payment:

Total amount: ₹2,100

Paid amount: ₹2,100

Balance amount: ₹0

Status: PAID

2. Payment Soft Deletion and Balance Reversal

Endpoint: DELETE /api/v1/payments/4

Result: Passed — returned 204 No Content.

After deleting Payment ID 4, the bill was recalculated:

Total amount: ₹2,100

Paid amount: ₹1,000

Balance amount: ₹1,100

Status: PARTIALLY_PAID

The payment record remained in MySQL with is_deleted = 1.

3. Deleted Payment Retrieval

Endpoint: GET /api/v1/payments/4

Result: Passed — returned 404 Not Found.

Error message: Payment not found with id: 4

This confirms soft-deleted payments are excluded from normal retrieval.

4. Duplicate Payment Reference Validation

Endpoint: POST /api/v1/payments

Test: Attempted to create a payment using the existing reference PAY-000003.

Result: Passed — returned 409 Conflict.

Error message: Payment reference already exists: PAY-000003

5. Overpayment Validation

Endpoint: POST /api/v1/payments

Test: Attempted to pay ₹1,200 when the outstanding balance was ₹1,100.

Result: Passed — payment was rejected with 409 Conflict.

Error message: Payment amount cannot exceed the outstanding balance

The bill's financial values remained unchanged after the rejected request.

6. Active Payment Listing

Endpoint: GET /api/v1/payments/bill/5?page=0&size=10

Result: Passed.

Active Payment ID 3 (PAY-000004, ₹1,000) appeared in the response.

Soft-deleted Payment ID 4 (PAY-000005, ₹1,100) was excluded.

The rejected overpayment did not create a payment record.

7. BillItem–Bill Integration

Tests performed:

Creating a BillItem increased the parent bill's subtotal and recalculated the total, balance, and payment status.

Deleting an eligible BillItem recalculated the parent bill correctly.

Deletion was rejected when the resulting bill total would be less than the amount already paid.

A rejected deletion returned 409 Conflict, and the bill's financial values remained unchanged.

Soft-deleted BillItems were excluded from normal retrieval.

Result: Passed for the scenarios tested.

8. MySQL Soft-Delete Verification

Verified that soft-deleted records remain stored in the database.

BillItems

IDs 2 and 3: is_deleted = 1

IDs 5 and 6: is_deleted = 0

Payments

Payment ID 4, reference PAY-000005, amount ₹1,100: is_deleted = 1

This confirms the tested records were soft-deleted rather than physically removed.

9. Final Bill Verification

Endpoint: GET /api/v1/bills/5

Final verified state:

Total amount: ₹2,100

Paid amount: ₹1,000

Balance amount: ₹1,100

Payment status: PARTIALLY_PAID

The final response matched the expected values.

10. Compilation

Command:

.\mvnw.cmd clean compile

Result: BUILD SUCCESS

Overall Result

The tested Billing scenarios passed, including payment creation, payment deletion and reversal, duplicate-reference rejection, overpayment rejection, payment listing, BillItem integration, and soft-delete verification.