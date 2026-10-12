package com.hms.service;

import com.hms.dto.request.DoctorRequest;
import com.hms.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;

public interface DoctorService {

    DoctorResponse createDoctor(DoctorRequest request);

    DoctorResponse getDoctorById(Long id);

    Page<DoctorResponse> getAllDoctors(int page, int size);

    DoctorResponse updateDoctor(Long id, DoctorRequest request);

    void deleteDoctor(Long id);
}