
package com.hms.controller;

import com.hms.dto.request.LabOrderItemRequest;
import com.hms.dto.response.LabOrderItemResponse;
import com.hms.service.LabOrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lab-order-items")
@RequiredArgsConstructor
public class LabOrderItemController {

    private final LabOrderItemService labOrderItemService;

    @PostMapping
    public ResponseEntity<LabOrderItemResponse> addLabOrderItem(
            @Valid @RequestBody LabOrderItemRequest request) {

        LabOrderItemResponse response =
                labOrderItemService.addLabOrderItem(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabOrderItemResponse> getLabOrderItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                labOrderItemService.getLabOrderItemById(id)
        );
    }

    @GetMapping("/by-order/{labOrderId}")
    public ResponseEntity<Page<LabOrderItemResponse>> getLabOrderItemsByOrder(
            @PathVariable Long labOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                labOrderItemService.getLabOrderItemsByLabOrderId(
                        labOrderId, page, size
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabOrderItem(
            @PathVariable Long id) {

        labOrderItemService.deleteLabOrderItem(id);

        return ResponseEntity.noContent().build();
    }
}
