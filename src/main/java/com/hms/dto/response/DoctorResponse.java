package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DoctorResponse {

    private Long id;
    private String doctorCode;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String email;
    private Integer experienceYears;
    private Double consultationFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}