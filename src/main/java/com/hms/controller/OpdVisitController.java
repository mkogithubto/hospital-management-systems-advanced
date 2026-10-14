
package com.hms.controller;

import com.hms.dto.request.OpdVisitRequest;
import com.hms.dto.response.OpdVisitResponse;
import com.hms.service.OpdVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.hms.dto.request.UpdateOpdVisitRequest;

@RestController
@RequestMapping("/api/v1/opd-visits")
@RequiredArgsConstructor
public class OpdVisitController {

    private final OpdVisitService opdVisitService;

    @PostMapping
    public ResponseEntity<OpdVisitResponse> createOpdVisit(
            @Valid @RequestBody OpdVisitRequest request) {

        OpdVisitResponse response =
                opdVisitService.createOpdVisit(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpdVisitResponse> getOpdVisitById(
            @PathVariable Long id) {

        OpdVisitResponse response =
                opdVisitService.getOpdVisitById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OpdVisitResponse>> getAllOpdVisits(
            @PageableDefault(size = 10, sort = "visitDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OpdVisitResponse> response =
                opdVisitService.getAllOpdVisits(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpdVisitResponse> updateOpdVisit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOpdVisitRequest request) {

        OpdVisitResponse response =
                opdVisitService.updateOpdVisit(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpdVisit(
            @PathVariable Long id) {

        opdVisitService.deleteOpdVisit(id);

        return ResponseEntity.noContent().build();
    }
}
