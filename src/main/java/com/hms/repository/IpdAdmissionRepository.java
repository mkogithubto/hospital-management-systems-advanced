
package com.hms.repository;

import com.hms.entity.IpdAdmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IpdAdmissionRepository
        extends JpaRepository<IpdAdmission, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM IpdAdmission a WHERE a.id = :id AND a.deleted = false")
    Optional<IpdAdmission> findByIdAndDeletedFalseForUpdate(
            @Param("id") Long id
    );

    boolean existsByAdmissionCode(String admissionCode);

    Optional<IpdAdmission> findByIdAndDeletedFalse(Long id);

    Page<IpdAdmission> findAllByDeletedFalse(Pageable pageable);

    boolean existsByBed_IdAndStatusAndDeletedFalse(
            Long bedId,
            String status
    );
}
