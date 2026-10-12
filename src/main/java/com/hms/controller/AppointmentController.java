package com.hms.controller;

import com.hms.dto.request.AppointmentRequest;
import com.hms.dto.response.AppointmentResponse;
import com.hms.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        AppointmentResponse response =
                appointmentService.createAppointment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id) {

        AppointmentResponse response =
                appointmentService.getAppointmentById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AppointmentResponse> response =
                appointmentService.getAllAppointments(page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {

        AppointmentResponse response =
                appointmentService.updateAppointment(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.noContent().build();
    }
}