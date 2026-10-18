
package com.hms.service.impl;

import com.hms.dto.request.BedRequest;
import com.hms.dto.response.BedResponse;
import com.hms.entity.Bed;
import com.hms.entity.Room;
import com.hms.repository.BedRepository;
import com.hms.repository.RoomRepository;
import com.hms.service.BedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class BedServiceImpl implements BedService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;

    public BedServiceImpl(
            BedRepository bedRepository,
            RoomRepository roomRepository
    ) {
        this.bedRepository = bedRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public BedResponse createBed(BedRequest request) {

        Room room = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active room not found with id: " + request.getRoomId()
                ));

        if (bedRepository.existsByRoom_IdAndBedNumber(
                request.getRoomId(), request.getBedNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bed number already exists in this room, including deleted beds"
            );
        }

        Bed bed = new Bed();
        bed.setBedNumber(request.getBedNumber());
        bed.setRoom(room);
        bed.setBedType(request.getBedType());
        bed.setStatus("AVAILABLE");

        Bed savedBed = bedRepository.save(bed);

        return mapToResponse(savedBed);
    }

    @Override
    @Transactional(readOnly = true)
    public BedResponse getBedById(Long id) {

        Bed bed = bedRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active bed not found with id: " + id
                ));

        return mapToResponse(bed);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BedResponse> getAllBeds(int page, int size) {

        if (page < 0 || size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater and size must be positive"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("bedNumber").ascending()
        );

        return bedRepository.findAllByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public BedResponse updateBed(Long id, BedRequest request) {

        Bed bed = bedRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active bed not found with id: " + id
                ));

        Room room = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active room not found with id: " + request.getRoomId()
                ));

        boolean duplicate = bedRepository.existsByRoom_IdAndBedNumber(
                request.getRoomId(), request.getBedNumber());

        if (duplicate && (
                !bed.getRoom().getId().equals(request.getRoomId())
                        || !bed.getBedNumber().equals(request.getBedNumber())
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bed number already exists in this room"
            );
        }

        bed.setBedNumber(request.getBedNumber());
        bed.setRoom(room);
        bed.setBedType(request.getBedType());

        Bed updatedBed = bedRepository.saveAndFlush(bed);
        return mapToResponse(updatedBed);
    }

    @Override
    public void deleteBed(Long id) {

        Bed bed = bedRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active bed not found with id: " + id
                ));

        bed.setDeleted(true);
        bedRepository.save(bed);
    }

    private BedResponse mapToResponse(Bed bed) {

        return new BedResponse(
                bed.getId(),
                bed.getBedNumber(),
                bed.getRoom().getId(),
                bed.getRoom().getRoomNumber(),
                bed.getBedType(),
                bed.getStatus(),
                bed.getCreatedAt(),
                bed.getUpdatedAt()
        );
    }
}
