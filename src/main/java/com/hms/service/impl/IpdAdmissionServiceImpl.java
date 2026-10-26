
package com.hms.service.impl;

import com.hms.dto.request.IpdAdmissionRequest;
import com.hms.dto.response.IpdAdmissionResponse;
import com.hms.dto.request.UpdateIpdAdmissionRequest;
import com.hms.entity.Doctor;
import com.hms.entity.IpdAdmission;
import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DoctorRepository;
import com.hms.repository.IpdAdmissionRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.IpdAdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hms.entity.Bed;
import com.hms.repository.BedRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class IpdAdmissionServiceImpl implements IpdAdmissionService {

    private final IpdAdmissionRepository ipdAdmissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final BedRepository bedRepository;

    @Override
    @Transactional
    public IpdAdmissionResponse createAdmission(IpdAdmissionRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + request.getPatientId()
                ));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + request.getDoctorId()
                ));

        Bed bed = bedRepository.findByIdAndDeletedFalseForUpdate(request.getBedId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active bed not found with ID: " + request.getBedId()
                ));

        if (!"AVAILABLE".equals(bed.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bed is not available. Current status: " + bed.getStatus()
            );
        }

        if (ipdAdmissionRepository.existsByBed_IdAndStatusAndDeletedFalse(
                bed.getId(), "ADMITTED")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bed is already assigned to an active admission"
            );
        }

        IpdAdmission admission = new IpdAdmission();

        admission.setAdmissionCode(generateAdmissionCode());
        admission.setPatient(patient);
        admission.setDoctor(doctor);
        admission.setBed(bed);
        admission.setAdmissionDate(request.getAdmissionDate());
        admission.setAdmissionReason(request.getAdmissionReason());
        admission.setDiagnosis(request.getDiagnosis());
        admission.setStatus("ADMITTED");
        admission.setDeleted(false);

        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        IpdAdmission savedAdmission = ipdAdmissionRepository.save(admission);

        return mapToResponse(savedAdmission);
    }


    @Override
    @Transactional(readOnly = true)
    public IpdAdmissionResponse getAdmissionById(Long id) {

        IpdAdmission admission = ipdAdmissionRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IPD admission not found with ID: " + id
                ));

        return mapToResponse(admission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IpdAdmissionResponse> getAllAdmissions(Pageable pageable) {

        return ipdAdmissionRepository
                .findAllByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }


    @Override
    @Transactional
    public IpdAdmissionResponse updateAdmission(
            Long id,
            UpdateIpdAdmissionRequest request) {

        IpdAdmission admission = ipdAdmissionRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IPD admission not found with ID: " + id
                ));

        admission.setAdmissionDate(request.getAdmissionDate());
        admission.setAdmissionReason(request.getAdmissionReason());
        admission.setDiagnosis(request.getDiagnosis());

        IpdAdmission updatedAdmission =
                ipdAdmissionRepository.saveAndFlush(admission);

        return mapToResponse(updatedAdmission);
    }


    @Override
    @Transactional
    public IpdAdmissionResponse dischargeAdmission(Long id) {

        IpdAdmission admission = ipdAdmissionRepository
                .findByIdAndDeletedFalseForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IPD admission not found with ID: " + id));

        if (!"ADMITTED".equals(admission.getStatus())) {
            throw new IllegalStateException(
                    "Only admitted patients can be discharged. Current status: "
                            + admission.getStatus());
        }

        // Release the assigned bed
        Bed bed = admission.getBed();
        bed.setStatus("AVAILABLE");
        bedRepository.save(bed);

        // Update admission details
        admission.setDischargeDate(java.time.LocalDateTime.now());
        admission.setStatus("DISCHARGED");

        IpdAdmission dischargedAdmission =
                ipdAdmissionRepository.saveAndFlush(admission);

        return mapToResponse(dischargedAdmission);
    }


    @Override
    @Transactional
    public void deleteAdmission(Long id) {

        IpdAdmission admission = ipdAdmissionRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IPD admission not found with ID: " + id
                ));

        admission.setDeleted(true);

        ipdAdmissionRepository.saveAndFlush(admission);
    }





    private IpdAdmissionResponse mapToResponse(IpdAdmission admission) {

        Patient patient = admission.getPatient();
        Doctor doctor = admission.getDoctor();
        Bed bed = admission.getBed();

        return IpdAdmissionResponse.builder()
                .id(admission.getId())
                .admissionCode(admission.getAdmissionCode())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .bedId(bed != null ? bed.getId() : null)
                .bedNumber(bed != null ? bed.getBedNumber() : null)
                .admissionDate(admission.getAdmissionDate())
                .dischargeDate(admission.getDischargeDate())
                .admissionReason(admission.getAdmissionReason())
                .diagnosis(admission.getDiagnosis())
                .status(admission.getStatus())
                .createdAt(admission.getCreatedAt())
                .updatedAt(admission.getUpdatedAt())
                .build();
    }


    private String generateAdmissionCode() {
        return "IPD-" + ThreadLocalRandom.current().nextInt(100000, 1000000);
    }
}
