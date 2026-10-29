
package com.hms.service.impl;

import com.hms.dto.request.PaymentRequest;
import com.hms.dto.response.PaymentResponse;
import com.hms.entity.Bill;
import com.hms.entity.Payment;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.BillRepository;
import com.hms.repository.PaymentRepository;
import com.hms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Bill bill = billRepository
                .findByIdAndDeletedFalseForUpdate(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + request.getBillId()
                ));

        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (request.getPaymentReference() != null
                && !request.getPaymentReference().isBlank()
                && paymentRepository.existsByPaymentReferenceAndDeletedFalse(
                request.getPaymentReference())) {
            throw new IllegalArgumentException(
                    "Payment reference already exists: "
                            + request.getPaymentReference()
            );
        }

        BigDecimal balance = bill.getBalanceAmount();

        if (balance == null || amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount cannot exceed the outstanding balance"
            );
        }

        BigDecimal paidAmount = bill.getPaidAmount().add(amount);
        BigDecimal balanceAmount = balance.subtract(amount);

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaymentReference(request.getPaymentReference());
        payment.setAmount(amount);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setTransactionNotes(request.getTransactionNotes());
        payment.setDeleted(false);

        Payment savedPayment = paymentRepository.save(payment);

        bill.setPaidAmount(paidAmount);
        bill.setBalanceAmount(balanceAmount);

        if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentStatus("PAID");
        } else {
            bill.setPaymentStatus("PARTIALLY_PAID");
        }

        billRepository.save(bill);

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id
                ));

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByBillId(
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

        return paymentRepository
                .findAllByBillIdAndDeletedFalse(
                        billId,
                        PageRequest.of(page, size)
                )
                .map(this::mapToResponse);
    }


    @Override
    public void deletePayment(Long id) {

        Payment payment = paymentRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id
                ));

        // Lock the associated bill to protect financial updates.
        Long billId = payment.getBill().getId();

        Bill bill = billRepository
                .findByIdAndDeletedFalseForUpdate(billId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + billId
                ));

        BigDecimal paymentAmount = payment.getAmount();
        BigDecimal paidAmount = bill.getPaidAmount();
        BigDecimal balanceAmount = bill.getBalanceAmount();

        if (paidAmount == null || balanceAmount == null
                || paidAmount.compareTo(paymentAmount) < 0) {
            throw new IllegalStateException(
                    "Cannot delete payment because bill payment totals are inconsistent"
            );
        }

        // Reverse the payment's effect on the bill.
        BigDecimal updatedPaidAmount = paidAmount.subtract(paymentAmount);
        BigDecimal updatedBalanceAmount = balanceAmount.add(paymentAmount);

        bill.setPaidAmount(updatedPaidAmount);
        bill.setBalanceAmount(updatedBalanceAmount);

        if (updatedPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentStatus("UNPAID");
        } else {
            bill.setPaymentStatus("PARTIALLY_PAID");
        }

        billRepository.save(bill);

        // Soft-delete the payment; retain its database record.
        payment.setDeleted(true);
        paymentRepository.save(payment);
    }


    private PaymentResponse mapToResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .billId(payment.getBill().getId())
                .billNumber(payment.getBill().getBillNumber())
                .paymentReference(payment.getPaymentReference())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .transactionNotes(payment.getTransactionNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
