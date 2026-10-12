package com.hms.service.impl;

import com.hms.dto.request.AppointmentRequest;
import com.hms.dto.response.AppointmentResponse;
import com.hms.entity.Appointment;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(
            AppointmentRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient with id "
                                        + request.getPatientId()
                                        + " not found"
                        )
                );

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor with id "
                                        + request.getDoctorId()
                                        + " not found"
                        )
                );

        Appointment appointment = Appointment.builder()
                .appointmentCode(generateAppointmentCode())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status("SCHEDULED")
                .reason(request.getReason())
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {

        Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment with id " + id + " not found"
                        )
                );

        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getAllAppointments(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Appointment> appointmentPage =
                appointmentRepository.findAllByDeletedFalse(pageable);

        return appointmentPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request) {

        Appointment appointment =
                appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment with id " + id + " not found"
                        )
                );

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient with id "
                                        + request.getPatientId()
                                        + " not found"
                        )
                );

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor with id "
                                        + request.getDoctorId()
                                        + " not found"
                        )
                );

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(updatedAppointment);
    }

    private String generateAppointmentCode() {

        long count = appointmentRepository.count();

        return String.format(
                "APT-%06d",
                count + 1
        );
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {

        Appointment appointment =
                appointmentRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment with id " + id + " not found"
                                )
                        );

        appointment.setDeleted(true);

        appointmentRepository.save(appointment);
    }

    private AppointmentResponse mapToResponse(
            Appointment appointment) {

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentCode(appointment.getAppointmentCode())

                .patientId(appointment.getPatient().getId())
                .patientName(
                        appointment.getPatient().getFirstName()
                                + " "
                                + appointment.getPatient().getLastName()
                )

                .doctorId(appointment.getDoctor().getId())
                .doctorName(
                        appointment.getDoctor().getFirstName()
                                + " "
                                + appointment.getDoctor().getLastName()
                )

                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())

                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())

                .build();
    }
}