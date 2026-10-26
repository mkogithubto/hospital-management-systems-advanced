HOSPITAL MANAGEMENT SYSTEM (HMS)
IPD–BED INTEGRATION MODULE NOTES

1. MODULE OVERVIEW

The IPD (In-Patient Department) module manages patient admissions, doctor assignments, bed allocation, and discharge. The Bed module manages bed availability and prevents invalid bed assignments.

The integration ensures that:
- New admissions are assigned to available beds.
- An occupied bed cannot be assigned to another active admission.
- Discharging a patient releases the assigned bed.
- Occupied beds cannot be deleted while an active admission exists.
- Soft-deleted beds are excluded from normal retrieval and listing.
- Historical admission records remain available after a bed is soft-deleted.


2. IMPORTANT API ENDPOINTS

BED APIs

GET    /api/v1/beds
Lists active beds.

GET    /api/v1/beds/{id}
Retrieves an active bed by ID.

POST   /api/v1/beds
Creates a new bed.

DELETE /api/v1/beds/{id}
Soft-deletes an eligible bed.

ROOM API

GET    /api/v1/rooms
Lists rooms.

PATIENT API

GET    /api/v1/patients
Lists patients.

DOCTOR API

GET    /api/v1/doctors
Lists doctors.

IPD ADMISSION APIs

POST   /api/v1/ipd-admissions
Creates an IPD admission.

GET    /api/v1/ipd-admissions
Lists active, non-soft-deleted admissions.

GET    /api/v1/ipd-admissions/{id}
Retrieves an active admission by ID.

PATCH  /api/v1/ipd-admissions/{id}/discharge
Discharges an admitted patient.

DELETE /api/v1/ipd-admissions/{id}
Soft-deletes an eligible admission.


3. IPD ADMISSION REQUEST FIELDS

The IpdAdmissionRequest DTO contains these fields:

patientId       Required
doctorId        Required
admissionDate   Required
admissionReason Required; maximum 500 characters
diagnosis       Optional; maximum 1000 characters
bedId           Required

Example request:

{
"patientId": 2,
"doctorId": 5,
"admissionDate": "2026-10-26T14:45:00",
"admissionReason": "Observation and treatment",
"diagnosis": "Under evaluation",
"bedId": 8
}

Use valid IDs and a suitable admission date when testing.


4. BED ASSIGNMENT WORKFLOW

Step 1: Create a bed.
The bed is initially assigned status AVAILABLE.

Step 2: Create an IPD admission using a valid patient ID, doctor ID, and available bed ID.

Step 3: If the admission succeeds:
- A new admission record is created.
- Admission status becomes ADMITTED.
- The assigned bed status becomes OCCUPIED.

Step 4: Attempting another admission using the same occupied bed should fail with HTTP 409 Conflict.

Step 5: Discharge the admitted patient.

After successful discharge:
- Admission status becomes DISCHARGED.
- Discharge date is populated.
- Bed status changes to AVAILABLE.

Step 6: The released bed can be assigned to another admission.


5. SOFT-DELETE RULES

The HMS project uses soft deletion instead of physically deleting database records.

Bed deletion:
- An occupied bed with an active IPD admission cannot be deleted.
- The API returns HTTP 409 Conflict for this scenario.
- An eligible bed can be soft-deleted.
- A soft-deleted bed is excluded from normal bed retrieval and listing.
- The database record is retained.

Historical admission preservation:
- Discharged admissions remain retrievable when they have not been soft-deleted.
- Historical admission responses preserve the previous bed ID and bed number, even if that bed is soft-deleted.
- Soft-deleted admissions are excluded from normal admission retrieval and listing.


6. EXCEPTION-HANDLING FIX

During manual testing, attempting to discharge an already discharged admission initially returned HTTP 500 Internal Server Error.

The service threw:

IllegalStateException:
Only admitted patients can be discharged. Current status: DISCHARGED

The existing GlobalExceptionHandler did not handle IllegalStateException.

A handler was added to GlobalExceptionHandler.java to return HTTP 409 Conflict with a structured JSON response.

Verified response:

{
"status": 409,
"error": "Conflict",
"message": "Only admitted patients can be discharged. Current status: DISCHARGED"
}

Important note:
The new handler applies to all IllegalStateException instances. A dedicated business-rule exception could be introduced later for more precise exception handling.


7. MANUAL POSTMAN TEST RESULTS

Test 1: Create a new bed
Result: Successful.
Bed ID: 8
Bed number: BED-004
Initial status: AVAILABLE

Test 2: Create an IPD admission
Result: Successful.
Admission ID: 6
Admission code: IPD-540147
Patient ID: 2
Doctor ID: 5
Bed ID: 8
Admission status: ADMITTED

Test 3: Verify bed after admission
Result: Successful.
BED-004 status changed to OCCUPIED.

Test 4: Admit another patient to the occupied bed
Result: HTTP 409 Conflict.
Message: Bed is not available. Current status: OCCUPIED

Test 5: Discharge admission ID 6
Result: Successful.
Admission status changed to DISCHARGED.

Test 6: Verify bed after discharge
Result: Successful.
BED-004 status changed to AVAILABLE.

Test 7: Reuse the discharged bed
Result: Successful.
New admission ID: 7
Admission code: IPD-218622
Bed ID: 8
Admission status: ADMITTED

Test 8: Delete occupied BED-004
Result: HTTP 409 Conflict.
Message: Cannot delete an occupied bed with an active IPD admission

Test 9: Discharge admission ID 7
Result: Successful.
Admission status changed to DISCHARGED.

Test 10: Verify BED-004 after discharge
Result: Successful.
Bed status was AVAILABLE.

Test 11: Soft-delete BED-004
Result: HTTP 204 No Content.

Test 12: Retrieve soft-deleted BED-004
Result: HTTP 404 Not Found.
Message: Active bed not found with id: 8

Test 13: List beds
Result: Successful.
BED-004 was excluded from the active bed listing.
BED-003 remained in the list.

Test 14: Retrieve admission ID 7 after bed deletion
Result: HTTP 200 OK.
The discharged admission and its historical bed details remained available.

Test 15: List IPD admissions
Result: Successful.
Six non-soft-deleted admissions were returned.
Admission ID 1 was excluded because it was soft-deleted.
Admissions ID 6 and ID 7 remained included because they were discharged but not soft-deleted.

Test 16: Discharge admission ID 7 a second time
Initial result: HTTP 500 Internal Server Error.
After the exception-handler fix: HTTP 409 Conflict.
Repeated discharge is correctly rejected.

Test 17: Retrieve nonexistent admission ID 999999
Result: HTTP 404 Not Found.
Message: IPD admission not found with ID: 999999

Test 18: Verify existing admission ID 5
Result: HTTP 200 OK.
Admission code: IPD-923519
Bed: BED-003
Status: ADMITTED
The existing admission remained intact.


8. BUILD AND AUTOMATED TEST STATUS

The following command succeeded:

.\mvnw.cmd clean compile

The following command also succeeded:

.\mvnw.cmd test

Automated test summary:
- Tests run: 1
- Failures: 0
- Errors: 0
- Skipped: 0

The existing automated test is HospitalManagementSystemsAdvancedApplicationTests.contextLoads().

Important:
The Postman scenarios above are manual API checks, not dedicated automated test cases.


9. REMAINING TESTING TASKS

The following areas still need testing:

- Concurrent attempts to assign the same bed.
- Missing or invalid patient IDs.
- Missing or invalid doctor IDs.
- Missing or invalid bed IDs.
- Invalid admission request fields and boundary lengths.
- Repeated bed deletion.
- Other invalid state transitions.
- Database-level verification of the soft-delete flag.
- Dedicated automated IPD–Bed service and controller tests.

Do not claim these scenarios are tested until they have actually been verified.


10. EXISTING DATA TO PRESERVE

Bed ID 8:
- Bed number: BED-004
- Soft-deleted after manual testing.

Admission ID 6:
- Admission code: IPD-540147
- Status: DISCHARGED
- Previously assigned to BED-004.

Admission ID 7:
- Admission code: IPD-218622
- Status: DISCHARGED
- Previously assigned to BED-004.

Admission ID 5:
- Admission code: IPD-923519
- Bed: BED-003
- Status: ADMITTED.

Do not discharge or modify admission ID 5 during unrelated testing.

Do not physically delete existing records to clean up test data. Preserve the project's soft-delete requirement.


11. MODULE COMPLETION CHECKLIST

[Completed] Create a bed through Postman.
[Completed] Create an IPD admission and assign a bed.
[Completed] Verify bed status changes to OCCUPIED.
[Completed] Reject a second admission to an occupied bed.
[Completed] Discharge a patient and release the bed.
[Completed] Reuse a discharged bed.
[Completed] Prevent deletion of an occupied bed.
[Completed] Verify soft deletion.
[Completed] Verify soft-deleted beds are excluded from retrieval and listing.
[Completed] Verify historical admission preservation.
[Completed] Fix repeated-discharge HTTP status handling.
[Completed] Verify nonexistent admission returns 404.
[Completed] Verify existing admission data remains intact.
[Pending] Add dedicated automated IPD–Bed tests.
[Pending] Test concurrent requests.
[Pending] Complete remaining validation and error scenarios.


END OF IPD–BED INTEGRATION NOTES