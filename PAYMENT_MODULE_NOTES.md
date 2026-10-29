# Payment Module — HMS

## 1. Overview
The Payment module records payments against bills, updates bill balances, validates payment amounts, and supports soft deletion.

## 2. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/payments` | Create a payment |
| GET | `/api/v1/payments/{id}` | Retrieve an active payment |
| GET | `/api/v1/payments/bill/{billId}?page=0&size=10` | List active payments for a bill |
| DELETE | `/api/v1/payments/{id}` | Soft-delete a payment |

## 3. Payment Processing
- The associated bill must exist and be active.
- Payment amount must be greater than zero.
- Duplicate active payment references are rejected.
- A payment cannot exceed the outstanding bill balance.
- Successful payment creation updates `paidAmount`, `balanceAmount`, and `paymentStatus`.
- The bill is locked during financial updates to help prevent concurrent payment inconsistencies.

## 4. Payment Status
- `UNPAID`: No amount has been paid and a balance remains.
- `PARTIALLY_PAID`: Some amount has been paid and a balance remains.
- `PAID`: The outstanding balance is zero.

## 5. Soft Deletion
Deleting a payment sets `is_deleted = true` rather than removing the database record. The payment amount is deducted from the bill's `paidAmount`, added back to its `balanceAmount`, and the bill's payment status is recalculated.

Deleted payments are excluded from normal retrieval and listing APIs.

## 6. Validation and Error Handling
- `400 Bad Request`: Request validation failures.
- `404 Not Found`: Payment or bill does not exist or is not active.
- `409 Conflict`: Duplicate payment reference or payment exceeding the outstanding balance.

## 7. Verification Completed
- Created a payment and verified bill balance updates.
- Retrieved payment by ID and listed payments with pagination.
- Verified duplicate payment reference and overpayment rejection.
- Soft-deleted a payment and verified the bill totals were restored.
- Confirmed deleted payments return 404 and are excluded from listing.
- Confirmed the payment record remains in MySQL with `is_deleted = 1`.

## 8. Key Files
- `entity/Payment.java`
- `repository/PaymentRepository.java`
- `dto/request/PaymentRequest.java`
- `dto/response/PaymentResponse.java`
- `service/PaymentService.java`
- `service/impl/PaymentServiceImpl.java`
- `controller/PaymentController.java`
