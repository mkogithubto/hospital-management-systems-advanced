
package com.hms.controller;

import com.hms.dto.request.MedicineBatchRequest;
import com.hms.dto.response.MedicineBatchResponse;
import com.hms.service.MedicineBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import com.hms.dto.request.MedicineBatchUpdateRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1/medicine-batches")
@RequiredArgsConstructor
public class MedicineBatchController {

    private final MedicineBatchService medicineBatchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineBatchResponse createMedicineBatch(
            @Valid @RequestBody MedicineBatchRequest request) {
        return medicineBatchService.createMedicineBatch(request);
    }


    @GetMapping("/{id}")
    public MedicineBatchResponse getMedicineBatchById(@PathVariable Long id) {
        return medicineBatchService.getMedicineBatchById(id);
    }


    @GetMapping
    public Page<MedicineBatchResponse> getAllMedicineBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return medicineBatchService.getAllMedicineBatches(page, size);
    }


    @PutMapping("/{id}")
    public MedicineBatchResponse updateMedicineBatch(
            @PathVariable Long id,
            @Valid @RequestBody MedicineBatchUpdateRequest request) {

        return medicineBatchService.updateMedicineBatch(id, request);
    }



    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicineBatch(@PathVariable Long id) {
        medicineBatchService.deleteMedicineBatch(id);
    }




}
