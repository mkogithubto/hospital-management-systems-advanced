
package com.hms.service;

import com.hms.dto.request.RoomRequest;
import com.hms.dto.response.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    RoomResponse createRoom(RoomRequest request);

    RoomResponse getRoomById(Long id);

    Page<RoomResponse> getAllRooms(Pageable pageable);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);
}
