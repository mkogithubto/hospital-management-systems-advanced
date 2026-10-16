
package com.hms.repository;

import com.hms.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumberAndDeletedFalse(String roomNumber);

    Optional<Room> findByIdAndDeletedFalse(Long id);

    Page<Room> findAllByDeletedFalse(Pageable pageable);
}
