
package com.hms.controller;

import com.hms.dto.request.LabTestRequest;
import com.hms.dto.response.LabTestResponse;
import com.hms.service.LabTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lab-tests")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping
    public ResponseEntity<LabTestResponse> createLabTest(
            @Valid @RequestBody LabTestRequest request) {

        LabTestResponse response = labTestService.createLabTest(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestResponse> getLabTestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(labTestService.getLabTestById(id));
    }

    @GetMapping
    public ResponseEntity<Page<LabTestResponse>> getAllLabTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(labTestService.getAllLabTests(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabTestResponse> updateLabTest(
            @PathVariable Long id,
            @Valid @RequestBody LabTestRequest request) {

        return ResponseEntity.ok(labTestService.updateLabTest(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabTest(@PathVariable Long id) {

        labTestService.deleteLabTest(id);

        return ResponseEntity.noContent().build();
    }
}
