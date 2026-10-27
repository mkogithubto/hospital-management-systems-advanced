
package com.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LabTestRequest {

    @NotBlank(message = "Test code is required")
    @Size(max = 50, message = "Test code must not exceed 50 characters")
    private String testCode;

    @NotBlank(message = "Test name is required")
    @Size(max = 150, message = "Test name must not exceed 150 characters")
    private String testName;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Sample type must not exceed 100 characters")
    private String sampleType;

    @NotNull(message = "Test price is required")
    @DecimalMin(value = "0.00", message = "Test price cannot be negative")
    private BigDecimal price;
}
