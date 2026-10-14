
package com.hms.service.impl;

import com.hms.dto.request.OpdVisitRequest;
import com.hms.dto.response.OpdVisitResponse;
import com.hms.entity.Doctor;
import com.hms.entity.OpdVisit;
import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DoctorRepository;
import com.hms.repository.OpdVisitRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.OpdVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hms.dto.request.UpdateOpdVisitRequest;
//import java.util.stream.Collectors;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OpdVisitServiceImpl implements OpdVisitService {

    private final OpdVisitRepository opdVisitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public OpdVisitResponse createOpdVisit(OpdVisitRequest request) {

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + request.getDoctorId()));

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + request.getPatientId()));

        String opdVisitCode = generateOpdVisitCode();

        OpdVisit opdVisit = OpdVisit.builder()
                .opdVisitCode(opdVisitCode)
                .patient(patient)
                .doctor(doctor)
                .visitDate(request.getVisitDate())
                .chiefComplaint(request.getChiefComplaint())
                .status("REGISTERED")
                .deleted(false)
                .build();

        OpdVisit savedVisit = opdVisitRepository.save(opdVisit);

        return OpdVisitResponse.builder()
                .id(savedVisit.getId())
                .opdVisitCode(savedVisit.getOpdVisitCode())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .visitDate(savedVisit.getVisitDate())
                .chiefComplaint(savedVisit.getChiefComplaint())
                .diagnosis(savedVisit.getDiagnosis())
                .status(savedVisit.getStatus())
                .createdAt(savedVisit.getCreatedAt())
                .updatedAt(savedVisit.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OpdVisitResponse getOpdVisitById(Long id) {

        OpdVisit opdVisit = opdVisitRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OPD visit not found with ID: " + id
                ));

        Patient patient = opdVisit.getPatient();
        Doctor doctor = opdVisit.getDoctor();

        return OpdVisitResponse.builder()
                .id(opdVisit.getId())
                .opdVisitCode(opdVisit.getOpdVisitCode())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .visitDate(opdVisit.getVisitDate())
                .chiefComplaint(opdVisit.getChiefComplaint())
                .diagnosis(opdVisit.getDiagnosis())
                .status(opdVisit.getStatus())
                .createdAt(opdVisit.getCreatedAt())
                .updatedAt(opdVisit.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpdVisitResponse> getAllOpdVisits(Pageable pageable) {

        return opdVisitRepository.findAllByDeletedFalse(pageable)
                .map(opdVisit -> {

                    Patient patient = opdVisit.getPatient();
                    Doctor doctor = opdVisit.getDoctor();

                    return OpdVisitResponse.builder()
                            .id(opdVisit.getId())
                            .opdVisitCode(opdVisit.getOpdVisitCode())
                            .patientId(patient.getId())
                            .patientName(patient.getFirstName() + " " + patient.getLastName())
                            .doctorId(doctor.getId())
                            .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                            .visitDate(opdVisit.getVisitDate())
                            .chiefComplaint(opdVisit.getChiefComplaint())
                            .diagnosis(opdVisit.getDiagnosis())
                            .status(opdVisit.getStatus())
                            .createdAt(opdVisit.getCreatedAt())
                            .updatedAt(opdVisit.getUpdatedAt())
                            .build();
                });
    }

    @Override
    @Transactional
    public OpdVisitResponse updateOpdVisit(
            Long id,
            UpdateOpdVisitRequest request) {

        OpdVisit opdVisit = opdVisitRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OPD visit not found with ID: " + id
                ));

        opdVisit.setVisitDate(request.getVisitDate());
        opdVisit.setChiefComplaint(request.getChiefComplaint());
        opdVisit.setDiagnosis(request.getDiagnosis());
        opdVisit.setStatus(request.getStatus());

        OpdVisit updatedVisit = opdVisitRepository.saveAndFlush(opdVisit);

        Patient patient = updatedVisit.getPatient();
        Doctor doctor = updatedVisit.getDoctor();

        return OpdVisitResponse.builder()
                .id(updatedVisit.getId())
                .opdVisitCode(updatedVisit.getOpdVisitCode())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .visitDate(updatedVisit.getVisitDate())
                .chiefComplaint(updatedVisit.getChiefComplaint())
                .diagnosis(updatedVisit.getDiagnosis())
                .status(updatedVisit.getStatus())
                .createdAt(updatedVisit.getCreatedAt())
                .updatedAt(updatedVisit.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteOpdVisit(Long id) {

        OpdVisit opdVisit = opdVisitRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OPD visit not found with ID: " + id
                ));

        opdVisit.setDeleted(true);

        opdVisitRepository.saveAndFlush(opdVisit);
    }

    private String generateOpdVisitCode() {
        String code;

        do {
            int randomNumber = ThreadLocalRandom.current()
                    .nextInt(1, 1_000_000);

            code = String.format("OPD-%06d", randomNumber);

        } while (opdVisitRepository.existsByOpdVisitCode(code));

        return code;
    }
}
