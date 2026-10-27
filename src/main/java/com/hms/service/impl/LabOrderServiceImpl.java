
package com.hms.service.impl;

import com.hms.dto.request.LabOrderRequest;
import com.hms.dto.response.LabOrderResponse;
import com.hms.entity.IpdAdmission;
import com.hms.entity.LabOrder;
import com.hms.entity.OpdVisit;
import com.hms.entity.Patient;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.IpdAdmissionRepository;
import com.hms.repository.LabOrderRepository;
import com.hms.repository.OpdVisitRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.LabOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabOrderServiceImpl implements LabOrderService {

    private final LabOrderRepository labOrderRepository;
    private final PatientRepository patientRepository;
    private final OpdVisitRepository opdVisitRepository;
    private final IpdAdmissionRepository ipdAdmissionRepository;

    @Override
    public LabOrderResponse createLabOrder(LabOrderRequest request) {

        validateEncounterSelection(request);

        if (labOrderRepository.existsByOrderCodeAndDeletedFalse(
                request.getOrderCode())) {
            throw new DuplicateResourceException(
                    "Lab order code already exists: " + request.getOrderCode()
            );
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()
                ));

        OpdVisit opdVisit = null;
        IpdAdmission ipdAdmission = null;

        if (request.getOpdVisitId() != null) {
            opdVisit = opdVisitRepository
                    .findByIdAndDeletedFalse(request.getOpdVisitId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "OPD visit not found with id: " + request.getOpdVisitId()
                    ));

            if (!opdVisit.getPatient().getId().equals(patient.getId())) {
                throw new IllegalArgumentException(
                        "The OPD visit does not belong to the specified patient"
                );
            }
        }

        if (request.getIpdAdmissionId() != null) {
            ipdAdmission = ipdAdmissionRepository
                    .findByIdAndDeletedFalse(request.getIpdAdmissionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "IPD admission not found with id: "
                                    + request.getIpdAdmissionId()
                    ));

            if (!ipdAdmission.getPatient().getId().equals(patient.getId())) {
                throw new IllegalArgumentException(
                        "The IPD admission does not belong to the specified patient"
                );
            }
        }

        LabOrder labOrder = LabOrder.builder()
                .orderCode(request.getOrderCode())
                .patient(patient)
                .opdVisit(opdVisit)
                .ipdAdmission(ipdAdmission)
                .status("ORDERED")
                .deleted(false)
                .build();

        return mapToResponse(labOrderRepository.save(labOrder));
    }

    @Override
    @Transactional(readOnly = true)
    public LabOrderResponse getLabOrderById(Long id) {

        LabOrder labOrder = labOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order not found with id: " + id
                ));

        return mapToResponse(labOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabOrderResponse> getAllLabOrders(int page, int size) {

        return labOrderRepository.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Override
    public void deleteLabOrder(Long id) {

        LabOrder labOrder = labOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order not found with id: " + id
                ));

        labOrder.setDeleted(true);
        labOrderRepository.save(labOrder);
    }

    private void validateEncounterSelection(LabOrderRequest request) {

        boolean hasOpdVisit = request.getOpdVisitId() != null;
        boolean hasIpdAdmission = request.getIpdAdmissionId() != null;

        if (hasOpdVisit == hasIpdAdmission) {
            throw new IllegalArgumentException(
                    "Provide exactly one of OPD visit ID or IPD admission ID"
            );
        }
    }

    private LabOrderResponse mapToResponse(LabOrder labOrder) {

        Patient patient = labOrder.getPatient();

        return LabOrderResponse.builder()
                .id(labOrder.getId())
                .orderCode(labOrder.getOrderCode())
                .patientId(patient.getId())
                .patientCode(patient.getPatientCode())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .opdVisitId(labOrder.getOpdVisit() != null
                        ? labOrder.getOpdVisit().getId() : null)
                .ipdAdmissionId(labOrder.getIpdAdmission() != null
                        ? labOrder.getIpdAdmission().getId() : null)
                .orderDate(labOrder.getOrderDate())
                .status(labOrder.getStatus())
                .createdAt(labOrder.getCreatedAt())
                .updatedAt(labOrder.getUpdatedAt())
                .build();
    }
}
