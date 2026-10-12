package com.hms.controller;

import com.hms.dto.request.DepartmentRequest;
import com.hms.dto.response.DepartmentResponse;
import com.hms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.createDepartment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @PathVariable Long id) {

        DepartmentResponse response =
                departmentService.getDepartmentById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DepartmentResponse> response =
                departmentService.getAllDepartments(page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.updateDepartment(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }
}