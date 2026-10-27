
package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabOrderRequest {

    @NotBlank(message = "Order code is required")
    @Size(max = 50, message = "Order code must not exceed 50 characters")
    private String orderCode;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long opdVisitId;

    private Long ipdAdmissionId;
}
