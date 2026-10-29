
package com.hms.service.impl;

import com.hms.dto.request.BillItemRequest;
import com.hms.dto.response.BillItemResponse;
import com.hms.entity.Bill;
import com.hms.entity.BillItem;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.BillItemRepository;
import com.hms.repository.BillRepository;
import com.hms.service.BillItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BillItemServiceImpl implements BillItemService {

    private final BillItemRepository billItemRepository;
    private final BillRepository billRepository;


    @Override
    public BillItemResponse createBillItem(BillItemRequest request) {

        Bill bill = billRepository
                .findByIdAndDeletedFalseForUpdate(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + request.getBillId()
                ));

        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1"
            );
        }

        if (request.getUnitPrice() == null
                || request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Unit price must be greater than zero"
            );
        }

        BigDecimal itemTotal = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        BigDecimal updatedSubtotal = bill.getSubtotal().add(itemTotal);

        // Validate the resulting bill totals before saving the item.
        validateBillTotals(bill, updatedSubtotal);

        BillItem billItem = new BillItem();
        billItem.setBill(bill);
        billItem.setItemName(request.getItemName());
        billItem.setDescription(request.getDescription());
        billItem.setQuantity(request.getQuantity());
        billItem.setUnitPrice(request.getUnitPrice());
        billItem.setTotalAmount(itemTotal);
        billItem.setDeleted(false);

        BillItem savedItem = billItemRepository.save(billItem);

        updateBillTotals(bill, updatedSubtotal);
        billRepository.save(bill);

        return mapToResponse(savedItem);
    }


    @Override
    @Transactional(readOnly = true)
    public BillItemResponse getBillItemById(Long id) {

        BillItem billItem = billItemRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill item not found with id: " + id
                ));

        return mapToResponse(billItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillItemResponse> getBillItemsByBillId(
            Long billId,
            int page,
            int size
    ) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be zero or greater and size must be greater than zero"
            );
        }

        billRepository.findByIdAndDeletedFalse(billId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + billId
                ));

        return billItemRepository
                .findAllByBillIdAndDeletedFalse(
                        billId,
                        PageRequest.of(page, size)
                )
                .map(this::mapToResponse);
    }


    @Override
    public void deleteBillItem(Long id) {

        BillItem billItem = billItemRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill item not found with id: " + id
                ));

        Long billId = billItem.getBill().getId();

        Bill bill = billRepository
                .findByIdAndDeletedFalseForUpdate(billId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + billId
                ));

        BigDecimal updatedSubtotal = bill.getSubtotal()
                .subtract(billItem.getTotalAmount());

        if (updatedSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Cannot delete bill item because the resulting subtotal would be negative"
            );
        }

        // Do not allow deletion if it would invalidate the bill's totals.
        validateBillTotals(bill, updatedSubtotal);

        updateBillTotals(bill, updatedSubtotal);
        billRepository.save(bill);

        // Soft-delete only; retain the item in the database.
        billItem.setDeleted(true);
        billItemRepository.save(billItem);
    }



    private void validateBillTotals(Bill bill, BigDecimal subtotal) {

        if (bill.getDiscountAmount() == null
                || bill.getTaxAmount() == null
                || bill.getPaidAmount() == null) {
            throw new IllegalStateException(
                    "Bill financial amounts must not be null"
            );
        }

        if (bill.getDiscountAmount().compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot exceed subtotal"
            );
        }

        BigDecimal totalAmount = subtotal
                .subtract(bill.getDiscountAmount())
                .add(bill.getTaxAmount());

        if (totalAmount.compareTo(bill.getPaidAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Bill total cannot be less than the amount already paid"
            );
        }
    }

    private void updateBillTotals(Bill bill, BigDecimal subtotal) {

        BigDecimal totalAmount = subtotal
                .subtract(bill.getDiscountAmount())
                .add(bill.getTaxAmount());

        BigDecimal balanceAmount = totalAmount
                .subtract(bill.getPaidAmount());

        bill.setSubtotal(subtotal);
        bill.setTotalAmount(totalAmount);
        bill.setBalanceAmount(balanceAmount);

        if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentStatus("PAID");
        } else if (bill.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            bill.setPaymentStatus("PARTIALLY_PAID");
        } else {
            bill.setPaymentStatus("UNPAID");
        }
    }



    private BillItemResponse mapToResponse(BillItem billItem) {

        return BillItemResponse.builder()
                .id(billItem.getId())
                .billId(billItem.getBill().getId())
                .itemName(billItem.getItemName())
                .description(billItem.getDescription())
                .quantity(billItem.getQuantity())
                .unitPrice(billItem.getUnitPrice())
                .totalAmount(billItem.getTotalAmount())
                .createdAt(billItem.getCreatedAt())
                .updatedAt(billItem.getUpdatedAt())
                .build();
    }
}
