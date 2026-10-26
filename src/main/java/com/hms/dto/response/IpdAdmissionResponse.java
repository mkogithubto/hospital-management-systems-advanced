
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IpdAdmissionResponse {

    private Long id;

    private String admissionCode;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private Long bedId;

    private String bedNumber;

    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;

    private String admissionReason;

    private String diagnosis;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
