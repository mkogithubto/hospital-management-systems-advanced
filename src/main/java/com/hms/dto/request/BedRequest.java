
package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BedRequest {

    @NotBlank(message = "Bed number is required")
    @Size(max = 20, message = "Bed number must not exceed 20 characters")
    private String bedNumber;

    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be greater than zero")
    private Long roomId;

    @NotBlank(message = "Bed type is required")
    @Size(max = 30, message = "Bed type must not exceed 30 characters")
    private String bedType;
}
