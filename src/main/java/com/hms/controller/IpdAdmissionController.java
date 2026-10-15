
package com.hms.controller;

import com.hms.dto.request.IpdAdmissionRequest;
import com.hms.dto.response.IpdAdmissionResponse;
import com.hms.dto.request.UpdateIpdAdmissionRequest;
import com.hms.service.IpdAdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ipd-admissions")
@RequiredArgsConstructor
public class IpdAdmissionController {

    private final IpdAdmissionService ipdAdmissionService;

    @PostMapping
    public ResponseEntity<IpdAdmissionResponse> createAdmission(
            @Valid @RequestBody IpdAdmissionRequest request) {

        IpdAdmissionResponse response =
                ipdAdmissionService.createAdmission(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IpdAdmissionResponse> getAdmissionById(
            @PathVariable Long id) {

        IpdAdmissionResponse response =
                ipdAdmissionService.getAdmissionById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<IpdAdmissionResponse>> getAllAdmissions(
            @PageableDefault(
                    size = 10,
                    sort = "admissionDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<IpdAdmissionResponse> response =
                ipdAdmissionService.getAllAdmissions(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IpdAdmissionResponse> updateAdmission(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIpdAdmissionRequest request) {

        IpdAdmissionResponse response =
                ipdAdmissionService.updateAdmission(id, request);

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/discharge")
    public ResponseEntity<IpdAdmissionResponse> dischargeAdmission(
            @PathVariable Long id) {

        IpdAdmissionResponse response =
                ipdAdmissionService.dischargeAdmission(id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmission(@PathVariable Long id) {

        ipdAdmissionService.deleteAdmission(id);

        return ResponseEntity.noContent().build();
    }

}
