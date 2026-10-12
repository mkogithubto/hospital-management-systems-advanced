package com.hms.service.impl;

import com.hms.dto.request.PatientRequest;
import com.hms.dto.response.PatientResponse;
import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.PatientRepository;
import com.hms.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hms.exception.DuplicateResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {

        // 1. Check whether phone number already exists
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "Patient with phone number "
                            + request.getPhone()
                            + " already exists"
            );
        }

        // 2. Create Patient entity
        Patient patient = Patient.builder()
                .patientCode(generatePatientCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .bloodGroup(request.getBloodGroup())
                .build();

        // 3. Save patient
        Patient savedPatient = patientRepository.save(patient);

        // 4. Convert Entity → Response DTO
        return mapToResponse(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient with id " + id + " not found"
                        )
                );

        return mapToResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Patient> patientPage =
                patientRepository.findAll(pageable);

        return patientPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long id, PatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient with id " + id + " not found"
                        )
                );

        if (!patient.getPhone().equals(request.getPhone())
                && patientRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException(
                    "Patient with phone number "
                            + request.getPhone()
                            + " already exists"
            );
        }

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setBloodGroup(request.getBloodGroup());

        Patient updatedPatient = patientRepository.save(patient);

        return mapToResponse(updatedPatient);
    }

    private String generatePatientCode() {

        long count = patientRepository.count();

        return String.format(
                "PAT-%06d",
                count + 1
        );
    }

    private PatientResponse mapToResponse(Patient patient) {

        return PatientResponse.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .bloodGroup(patient.getBloodGroup())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient with id " + id + " not found"
                        )
                );

        patientRepository.delete(patient);
    }
}