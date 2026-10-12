package com.hms.controller;

import com.hms.dto.request.PatientRequest;
import com.hms.dto.response.PatientResponse;
import com.hms.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response =
                patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id) {

        PatientResponse response =
                patientService.getPatientById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PatientResponse> response =
                patientService.getAllPatients(page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response =
                patientService.updatePatient(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long id) {

        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }
}