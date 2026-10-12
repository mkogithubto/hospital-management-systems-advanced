package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PatientResponse {

    private Long id;

    private String patientCode;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    private String email;

    private String address;

    private String bloodGroup;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}