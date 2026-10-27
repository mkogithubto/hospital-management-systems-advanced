
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LabOrderResponse {

    private Long id;

    private String orderCode;

    private Long patientId;

    private String patientCode;

    private String patientName;

    private Long opdVisitId;

    private Long ipdAdmissionId;

    private LocalDateTime orderDate;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
