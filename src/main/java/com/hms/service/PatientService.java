package com.hms.service;

import com.hms.dto.request.PatientRequest;
import com.hms.dto.response.PatientResponse;
import org.springframework.data.domain.Page;

public interface PatientService {

    PatientResponse createPatient(PatientRequest request);

    PatientResponse getPatientById(Long id);

    Page<PatientResponse> getAllPatients(int page, int size);

    PatientResponse updatePatient(Long id, PatientRequest request);

    void deletePatient(Long id);
}