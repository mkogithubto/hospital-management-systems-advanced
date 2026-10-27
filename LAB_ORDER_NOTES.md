
# Lab Order Module

## Overview
Manages laboratory orders associated with patients' OPD visits or IPD admissions.

## Entity
- Entity: `LabOrder`
- Table: `lab_orders`
- Unique field: `order_code`
- Soft-delete field: `is_deleted`
- Initial status: `ORDERED`

## Relationships
- Patient: mandatory
- OPD visit: optional
- IPD admission: optional
- Exactly one of OPD visit or IPD admission must be supplied.
- The selected encounter must belong to the specified patient.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/lab-orders` | Create a lab order |
| GET | `/api/v1/lab-orders/{id}` | Retrieve a lab order |
| GET | `/api/v1/lab-orders?page=0&size=10` | Paginated listing |
| DELETE | `/api/v1/lab-orders/{id}` | Soft-delete a lab order |

## Components
- `LabOrder.java`
- `LabOrderRepository.java`
- `LabOrderRequest.java`
- `LabOrderResponse.java`
- `LabOrderService.java`
- `LabOrderServiceImpl.java`
- `LabOrderController.java`

## Validation and Business Rules
- Order code is required and limited to 50 characters.
- Patient ID is required.
- Duplicate active order codes are rejected.
- Exactly one encounter ID must be supplied.
- The encounter must belong to the specified patient.
- Soft-deleted encounters cannot be selected through the filtered lookup.
- Deleted orders are excluded from normal retrieval and pagination.
- Delete operations mark records as deleted rather than physically removing them.
- Creation and update timestamps are managed by entity lifecycle callbacks.

## Verification
- Maven clean compile: passed.
- Create order: `201 Created`.
- Get order by ID: `200 OK`.
- Paginated listing: `200 OK`.
- Invalid encounter selection: rejected.
- Patient–encounter mismatch: rejected.
- Soft deletion: `204 No Content`.
- Deleted order lookup: `404 Not Found`.
- Deleted order excluded from pagination: confirmed.
