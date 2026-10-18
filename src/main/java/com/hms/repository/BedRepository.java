
package com.hms.repository;

import com.hms.entity.Bed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {

    Optional<Bed> findByIdAndDeletedFalse(Long id);

    Page<Bed> findAllByDeletedFalse(Pageable pageable);

    // Checks all beds, including soft-deleted beds
    boolean existsByRoom_IdAndBedNumber(Long roomId, String bedNumber);

    // Checks only active beds
    boolean existsByRoom_IdAndBedNumberAndDeletedFalse(
            Long roomId,
            String bedNumber
    );
}
