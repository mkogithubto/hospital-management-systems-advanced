
package com.hms.repository;

import com.hms.entity.Bed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bed b WHERE b.id = :id AND b.deleted = false")
    Optional<Bed> findByIdAndDeletedFalseForUpdate(@Param("id") Long id);

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
