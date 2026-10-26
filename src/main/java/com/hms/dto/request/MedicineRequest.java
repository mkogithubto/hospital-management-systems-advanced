
package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineRequest {

    @NotBlank(message = "Medicine code is required")
    @Size(max = 50, message = "Medicine code must not exceed 50 characters")
    private String medicineCode;

    @NotBlank(message = "Medicine name is required")
    @Size(max = 150, message = "Medicine name must not exceed 150 characters")
    private String name;

    @Size(max = 150, message = "Generic name must not exceed 150 characters")
    private String genericName;

    @Size(max = 150, message = "Manufacturer must not exceed 150 characters")
    private String manufacturer;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Size(max = 50, message = "Strength must not exceed 50 characters")
    private String strength;

    @Size(max = 50, message = "Dosage form must not exceed 50 characters")
    private String dosageForm;

    private boolean prescriptionRequired;
}
