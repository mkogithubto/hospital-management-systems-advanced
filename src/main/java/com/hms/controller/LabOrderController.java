
package com.hms.controller;

import com.hms.dto.request.LabOrderRequest;
import com.hms.dto.response.LabOrderResponse;
import com.hms.service.LabOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lab-orders")
@RequiredArgsConstructor
public class LabOrderController {

    private final LabOrderService labOrderService;

    @PostMapping
    public ResponseEntity<LabOrderResponse> createLabOrder(
            @Valid @RequestBody LabOrderRequest request) {

        LabOrderResponse response = labOrderService.createLabOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabOrderResponse> getLabOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(labOrderService.getLabOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<LabOrderResponse>> getAllLabOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                labOrderService.getAllLabOrders(page, size)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabOrder(@PathVariable Long id) {

        labOrderService.deleteLabOrder(id);

        return ResponseEntity.noContent().build();
    }
}
