
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MedicineResponse {

    private Long id;

    private String medicineCode;

    private String name;

    private String genericName;

    private String manufacturer;

    private String category;

    private String strength;

    private String dosageForm;

    private boolean prescriptionRequired;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
