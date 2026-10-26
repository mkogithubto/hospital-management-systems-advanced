
package com.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MedicineBatchUpdateRequest {

    @NotBlank(message = "Batch number is required")
    @Size(max = 50, message = "Batch number must not exceed 50 characters")
    private String batchNumber;

    private LocalDate manufacturingDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.00", message = "Purchase price cannot be negative")
    private BigDecimal purchasePrice;
}
