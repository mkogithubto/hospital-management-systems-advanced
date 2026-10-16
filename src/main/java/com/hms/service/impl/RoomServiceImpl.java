
package com.hms.service.impl;

import com.hms.dto.request.RoomRequest;
import com.hms.dto.response.RoomResponse;
import com.hms.entity.Room;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.RoomRepository;
import com.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {

        if (roomRepository.existsByRoomNumberAndDeletedFalse(
                request.getRoomNumber())) {
            throw new IllegalArgumentException(
                    "Room number already exists: " + request.getRoomNumber()
            );
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setFloor(request.getFloor());

        // New rooms must always start as AVAILABLE.
        room.setStatus("AVAILABLE");

        room.setDeleted(false);

        Room savedRoom = roomRepository.saveAndFlush(room);

        return mapToResponse(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {

        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id
                ));

        return mapToResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAllRooms(Pageable pageable) {

        return roomRepository.findAllByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {

        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id
                ));

        boolean roomNumberChanged =
                !room.getRoomNumber().equals(request.getRoomNumber());

        if (roomNumberChanged &&
                roomRepository.existsByRoomNumberAndDeletedFalse(
                        request.getRoomNumber())) {
            throw new IllegalArgumentException(
                    "Room number already exists: " + request.getRoomNumber()
            );
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setFloor(request.getFloor());

        // Room status is managed by the service, not the request.
        Room updatedRoom = roomRepository.saveAndFlush(room);

        return mapToResponse(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {

        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id
                ));

        room.setDeleted(true);

        roomRepository.saveAndFlush(room);
    }

    private RoomResponse mapToResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .floor(room.getFloor())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
