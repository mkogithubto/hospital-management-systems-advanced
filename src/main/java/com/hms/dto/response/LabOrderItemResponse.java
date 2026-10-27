
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LabOrderItemResponse {

    private Long id;

    private Long labOrderId;

    private String orderCode;

    private Long labTestId;

    private String testCode;

    private String testName;

    private BigDecimal priceAtOrder;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
