
package com.hms.dto.response;

import java.time.LocalDateTime;

public record BedResponse(
        Long id,
        String bedNumber,
        Long roomId,
        String roomNumber,
        String bedType,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
