
package com.hms.controller;

import com.hms.dto.request.BedRequest;
import com.hms.dto.response.BedResponse;
import com.hms.service.BedService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/beds")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @PostMapping
    public ResponseEntity<BedResponse> createBed(
            @Valid @RequestBody BedRequest request
    ) {
        BedResponse response = bedService.createBed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BedResponse> getBedById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bedService.getBedById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BedResponse>> getAllBeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bedService.getAllBeds(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BedResponse> updateBed(
            @PathVariable Long id,
            @Valid @RequestBody BedRequest request
    ) {
        return ResponseEntity.ok(bedService.updateBed(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBed(
            @PathVariable Long id
    ) {
        bedService.deleteBed(id);
        return ResponseEntity.noContent().build();
    }
}
