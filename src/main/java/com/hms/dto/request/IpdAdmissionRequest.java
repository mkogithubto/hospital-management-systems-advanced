
package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IpdAdmissionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Admission date is required")
    private LocalDateTime admissionDate;

    @NotBlank(message = "Admission reason is required")
    @Size(max = 500, message = "Admission reason must not exceed 500 characters")
    private String admissionReason;

    @Size(max = 1000, message = "Diagnosis must not exceed 1000 characters")
    private String diagnosis;

    @NotNull(message = "Bed ID is required")
    private Long bedId;
}
