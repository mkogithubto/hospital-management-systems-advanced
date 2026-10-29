
package com.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Bill ID is required")
    private Long billId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01",
            message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Size(max = 30,
            message = "Payment method cannot exceed 30 characters")
    private String paymentMethod;

    @Size(max = 100,
            message = "Payment reference cannot exceed 100 characters")
    private String paymentReference;

    private LocalDateTime paymentDate;

    @Size(max = 500,
            message = "Transaction notes cannot exceed 500 characters")
    private String transactionNotes;
}
