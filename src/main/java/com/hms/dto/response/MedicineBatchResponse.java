
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MedicineBatchResponse {

    private Long id;

    private Long medicineId;

    private String medicineCode;

    private String medicineName;

    private String batchNumber;

    private LocalDate manufacturingDate;

    private LocalDate expiryDate;

    private Integer quantityReceived;

    private Integer availableQuantity;

    private BigDecimal purchasePrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
