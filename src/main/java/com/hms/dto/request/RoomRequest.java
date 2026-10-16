
package com.hms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Room number must not exceed 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9-]+$",
            message = "Room number can contain only letters, numbers, and hyphens"
    )
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    @Pattern(
            regexp = "GENERAL|PRIVATE|SEMI_PRIVATE|ICU",
            message = "Room type must be GENERAL, PRIVATE, SEMI_PRIVATE, or ICU"
    )
    private String roomType;

    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor cannot be negative")
    private Integer floor;

    @NotBlank(message = "Room status is required")
    @Pattern(
            regexp = "AVAILABLE|OCCUPIED|MAINTENANCE",
            message = "Room status must be AVAILABLE, OCCUPIED, or MAINTENANCE"
    )
    private String status;
}
