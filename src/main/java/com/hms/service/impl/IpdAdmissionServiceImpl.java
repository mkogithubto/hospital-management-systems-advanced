
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

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class IpdAdmissionServiceImpl implements IpdAdmissionService {

    private final IpdAdmissionRepository ipdAdmissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

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

        IpdAdmission admission = new IpdAdmission();

        admission.setAdmissionCode(generateAdmissionCode());
        admission.setPatient(patient);
        admission.setDoctor(doctor);
        admission.setAdmissionDate(request.getAdmissionDate());
        admission.setAdmissionReason(request.getAdmissionReason());
        admission.setDiagnosis(request.getDiagnosis());
        admission.setStatus("ADMITTED");
        admission.setDeleted(false);

        IpdAdmission savedAdmission =
                ipdAdmissionRepository.save(admission);

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
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IPD admission not found with ID: " + id
                ));

        if (!"ADMITTED".equals(admission.getStatus())) {
            throw new IllegalStateException(
                    "Only admitted patients can be discharged. Current status: "
                            + admission.getStatus()
            );
        }

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

        return IpdAdmissionResponse.builder()
                .id(admission.getId())
                .admissionCode(admission.getAdmissionCode())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
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
