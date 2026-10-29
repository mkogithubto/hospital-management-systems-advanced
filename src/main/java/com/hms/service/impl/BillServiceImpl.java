
package com.hms.service.impl;

import com.hms.dto.request.BillRequest;
import com.hms.dto.response.BillResponse;
import com.hms.entity.Bill;
import com.hms.entity.IpdAdmission;
import com.hms.entity.OpdVisit;
import com.hms.entity.Patient;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.BillRepository;
import com.hms.repository.IpdAdmissionRepository;
import com.hms.repository.OpdVisitRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final OpdVisitRepository opdVisitRepository;
    private final IpdAdmissionRepository ipdAdmissionRepository;

    @Override
    public BillResponse createBill(BillRequest request) {

        if (billRepository.existsByBillNumberAndDeletedFalse(
                request.getBillNumber())) {
            throw new DuplicateResourceException(
                    "Bill number already exists: " + request.getBillNumber());
        }

        boolean hasOpdVisit = request.getOpdVisitId() != null;
        boolean hasIpdAdmission = request.getIpdAdmissionId() != null;

        if (hasOpdVisit == hasIpdAdmission) {
            throw new IllegalArgumentException(
                    "Provide exactly one of OPD visit ID or IPD admission ID");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        OpdVisit opdVisit = null;
        IpdAdmission ipdAdmission = null;

        if (hasOpdVisit) {
            opdVisit = opdVisitRepository
                    .findByIdAndDeletedFalse(request.getOpdVisitId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "OPD visit not found with id: "
                                    + request.getOpdVisitId()));

            if (!opdVisit.getPatient().getId().equals(patient.getId())) {
                throw new IllegalArgumentException(
                        "The OPD visit does not belong to the specified patient");
            }
        }

        if (hasIpdAdmission) {
            ipdAdmission = ipdAdmissionRepository
                    .findByIdAndDeletedFalse(request.getIpdAdmissionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "IPD admission not found with id: "
                                    + request.getIpdAdmissionId()));

            if (!ipdAdmission.getPatient().getId().equals(patient.getId())) {
                throw new IllegalArgumentException(
                        "The IPD admission does not belong to the specified patient");
            }
        }

        BigDecimal subtotal = request.getSubtotal();
        BigDecimal discount = request.getDiscountAmount();
        BigDecimal tax = request.getTaxAmount();

        if (subtotal == null || discount == null || tax == null) {
            throw new IllegalArgumentException(
                    "Subtotal, discount amount, and tax amount are required");
        }

        if (subtotal.signum() < 0
                || discount.signum() < 0
                || tax.signum() < 0) {
            throw new IllegalArgumentException(
                    "Subtotal, discount amount, and tax amount cannot be negative");
        }

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot exceed subtotal");
        }

        BigDecimal totalAmount = subtotal
                .subtract(discount)
                .add(tax);

        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal balanceAmount = totalAmount;

        String paymentStatus = totalAmount.signum() == 0
                ? "PAID"
                : "UNPAID";

        Bill bill = new Bill();
        bill.setBillNumber(request.getBillNumber());
        bill.setPatient(patient);
        bill.setOpdVisit(opdVisit);
        bill.setIpdAdmission(ipdAdmission);
        bill.setBillDate(request.getBillDate());
        bill.setSubtotal(subtotal);
        bill.setDiscountAmount(discount);
        bill.setTaxAmount(tax);
        bill.setTotalAmount(totalAmount);
        bill.setPaidAmount(paidAmount);
        bill.setBalanceAmount(balanceAmount);
        bill.setPaymentStatus(paymentStatus);
        bill.setDeleted(false);

        Bill savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {

        Bill bill = billRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + id));

        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillResponse> getAllBills(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be positive");
        }

        return billRepository
                .findAllByDeletedFalse(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Override
    public void deleteBill(Long id) {

        Bill bill = billRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with id: " + id));

        bill.setDeleted(true);
        billRepository.save(bill);
    }

    private BillResponse mapToResponse(Bill bill) {

        Patient patient = bill.getPatient();

        String patientName = patient.getFirstName() + " "
                + patient.getLastName();

        return BillResponse.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .patientId(patient.getId())
                .patientCode(patient.getPatientCode())
                .patientName(patientName)
                .opdVisitId(bill.getOpdVisit() != null
                        ? bill.getOpdVisit().getId() : null)
                .ipdAdmissionId(bill.getIpdAdmission() != null
                        ? bill.getIpdAdmission().getId() : null)
                .billDate(bill.getBillDate())
                .subtotal(bill.getSubtotal())
                .discountAmount(bill.getDiscountAmount())
                .taxAmount(bill.getTaxAmount())
                .totalAmount(bill.getTotalAmount())
                .paidAmount(bill.getPaidAmount())
                .balanceAmount(bill.getBalanceAmount())
                .paymentStatus(bill.getPaymentStatus())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }
}
