
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private Long id;

    private Long billId;

    private String billNumber;

    private String paymentReference;

    private BigDecimal amount;

    private String paymentMethod;

    private LocalDateTime paymentDate;

    private String transactionNotes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
