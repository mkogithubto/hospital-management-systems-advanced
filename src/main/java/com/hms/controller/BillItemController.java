
package com.hms.controller;

import com.hms.dto.request.BillItemRequest;
import com.hms.dto.response.BillItemResponse;
import com.hms.service.BillItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bill-items")
@RequiredArgsConstructor
public class BillItemController {

    private final BillItemService billItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillItemResponse createBillItem(
            @Valid @RequestBody BillItemRequest request
    ) {
        return billItemService.createBillItem(request);
    }

    @GetMapping("/{id}")
    public BillItemResponse getBillItemById(
            @PathVariable Long id
    ) {
        return billItemService.getBillItemById(id);
    }

    @GetMapping("/bill/{billId}")
    public Page<BillItemResponse> getBillItemsByBillId(
            @PathVariable Long billId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return billItemService.getBillItemsByBillId(billId, page, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBillItem(@PathVariable Long id) {
        billItemService.deleteBillItem(id);
    }
}
