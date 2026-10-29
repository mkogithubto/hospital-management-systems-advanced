
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class BillResponse {

    private Long id;

    private String billNumber;

    private Long patientId;

    private String patientCode;

    private String patientName;

    private Long opdVisitId;

    private Long ipdAdmissionId;

    private LocalDateTime billDate;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;

    private String paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
