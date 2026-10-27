
# Lab Test Master Module

## Overview
Manages laboratory test definitions in the Hospital Management System.

## Entity
- `LabTest`
- Table: `lab_tests`
- Unique field: `test_code`
- Soft-delete field: `is_deleted`
- Automatically managed timestamps: `created_at`, `updated_at`

## Main Fields
- `testCode`
- `testName`
- `category`
- `description`
- `sampleType`
- `price`
- `status`
- `deleted`
- `createdAt`
- `updatedAt`

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/lab-tests` | Create a lab test |
| GET | `/api/v1/lab-tests/{id}` | Retrieve a lab test |
| GET | `/api/v1/lab-tests?page=0&size=10` | Paginated listing |
| PUT | `/api/v1/lab-tests/{id}` | Update a lab test |
| DELETE | `/api/v1/lab-tests/{id}` | Soft-delete a lab test |

## Validation and Business Rules
- Test code and test name are required.
- Test code must not exceed 50 characters.
- Test name must not exceed 150 characters.
- Price is required and cannot be negative.
- Duplicate active test codes are rejected.
- Deleted records are excluded from normal ID lookups and listings.
- Delete operations set `is_deleted = 1`; records are not physically removed.
- `created_at` and `updated_at` are managed by entity lifecycle callbacks.

## Components
- Entity: `LabTest`
- Repository: `LabTestRepository`
- Request DTO: `LabTestRequest`
- Response DTO: `LabTestResponse`
- Service: `LabTestService`
- Service implementation: `LabTestServiceImpl`
- Controller: `LabTestController`

## Verification
- Maven clean compile: passed.
- Postman CRUD tests: passed.
- Duplicate-code validation: passed with `409 Conflict`.
- Soft-delete verification: passed.
- Deleted record lookup: `404 Not Found`.
- Deleted record excluded from pagination: confirmed.
- MySQL confirmed that the record remains with `is_deleted = 1`.
