package com.hms.service.impl;

import com.hms.dto.request.DoctorRequest;
import com.hms.dto.response.DoctorResponse;
import com.hms.entity.Doctor;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DoctorRepository;
import com.hms.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {

        // Check duplicate phone number
        if (doctorRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "Doctor with phone number "
                            + request.getPhone()
                            + " already exists"
            );
        }

        // Check duplicate email
        if (request.getEmail() != null
                && doctorRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Doctor with email "
                            + request.getEmail()
                            + " already exists"
            );
        }

        Doctor doctor = Doctor.builder()
                .doctorCode(generateDoctorCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .email(request.getEmail())
                .experienceYears(request.getExperienceYears())
                .consultationFee(request.getConsultationFee())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return mapToResponse(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> getAllDoctors(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Doctor> doctorPage =
                doctorRepository.findAll(pageable);

        return doctorPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        if (!doctor.getPhone().equals(request.getPhone())
                && doctorRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException(
                    "Doctor with phone number "
                            + request.getPhone()
                            + " already exists"
            );
        }

        if (request.getEmail() != null
                && !request.getEmail().equals(doctor.getEmail())
                && doctorRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Doctor with email "
                            + request.getEmail()
                            + " already exists"
            );
        }

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());
        doctor.setEmail(request.getEmail());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        return mapToResponse(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        doctorRepository.delete(doctor);
    }

    private String generateDoctorCode() {

        long count = doctorRepository.count();

        return String.format("DOC-%06d", count + 1);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {

        return DoctorResponse.builder()
                .id(doctor.getId())
                .doctorCode(doctor.getDoctorCode())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .email(doctor.getEmail())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }


}