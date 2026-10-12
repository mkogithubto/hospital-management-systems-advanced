package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;
    private String appointmentCode;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private LocalDate appointmentDate;
    private String appointmentTime;
    private String status;
    private String reason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}