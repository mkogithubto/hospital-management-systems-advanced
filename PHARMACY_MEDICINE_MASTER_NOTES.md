# Pharmacy Management — Medicine Master Notes

## 1. Overview

The Medicine Master stores the basic information about medicines in the Hospital Management System.

## 2. Implemented Components

- **Entity:** `Medicine.java`
- **Repository:** `MedicineRepository.java`
- **Request DTO:** `MedicineRequest.java`
- **Response DTO:** `MedicineResponse.java`
- **Service interface:** `MedicineService.java`
- **Service implementation:** `MedicineServiceImpl.java`
- **Controller:** `MedicineController.java`

## 3. Medicine Fields

- ID
- Medicine code
- Name
- Generic name
- Manufacturer
- Category
- Strength
- Dosage form
- Prescription required
- Status
- Created timestamp
- Updated timestamp
- Soft-delete flag

## 4. API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/medicines` | Create a medicine |
| GET | `/api/v1/medicines/{id}` | Retrieve an active medicine by ID |
| GET | `/api/v1/medicines` | Retrieve active medicines with pagination |
| PUT | `/api/v1/medicines/{id}` | Update medicine details |
| DELETE | `/api/v1/medicines/{id}` | Soft-delete a medicine |

## 5. Business Rules

- Medicine code and name are required.
- Request DTO validation checks required fields and maximum lengths.
- Duplicate active medicine codes are rejected.
- New medicines receive the `ACTIVE` status.
- Missing or soft-deleted medicines are excluded from normal retrieval.
- Deletion sets the `deleted` flag to `true`; records are not physically removed.
- Created and updated timestamps are maintained by JPA lifecycle callbacks.
- The database unique constraint prevents reusing a medicine code even if its previous record was soft-deleted.

## 6. Testing Completed

- Successful medicine creation.
- Duplicate medicine code returns `409 Conflict`.
- Retrieval of an existing medicine.
- Retrieval of a nonexistent medicine returns `404 Not Found`.
- Paginated medicine listing.
- Successful medicine update and verification through GET.
- Soft deletion returns `204 No Content`.
- Soft-deleted medicine is excluded from GET-by-ID and list APIs.
- Blank medicine name returns `400 Bad Request`.
- Blank medicine code returns `400 Bad Request`.

## 7. Next Development Phase

Implement Medicine Batch and Inventory incrementally, including batch numbers, expiry dates, stock quantities, and stock validation.

## 8. Project Conventions

- Use soft deletion instead of physical deletion.
- Exclude soft-deleted records from normal retrieval and listing.
- Implement and test one service method and API at a time.
- Add concise documentation when each module is complete.
