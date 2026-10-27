
package com.hms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabOrderItemRequest {

    @NotNull(message = "Lab order ID is required")
    private Long labOrderId;

    @NotNull(message = "Lab test ID is required")
    private Long labTestId;
}
