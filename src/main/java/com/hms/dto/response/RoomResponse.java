
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private String roomType;

    private Integer floor;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
