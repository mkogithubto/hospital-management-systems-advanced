package com.hms.controller;

import com.hms.dto.request.DoctorRequest;
import com.hms.dto.response.DoctorResponse;
import com.hms.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorRequest request) {

        DoctorResponse response =
                doctorService.createDoctor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(
            @PathVariable Long id) {

        DoctorResponse response =
                doctorService.getDoctorById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DoctorResponse> response =
                doctorService.getAllDoctors(page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest request) {

        DoctorResponse response =
                doctorService.updateDoctor(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}