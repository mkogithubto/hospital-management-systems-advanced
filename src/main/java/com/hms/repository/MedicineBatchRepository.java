
package com.hms.repository;

import com.hms.entity.MedicineBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Long> {

    Optional<MedicineBatch> findByIdAndDeletedFalse(Long id);

    boolean existsByMedicineIdAndBatchNumberAndDeletedFalse(
            Long medicineId,
            String batchNumber
    );

    Page<MedicineBatch> findAllByDeletedFalse(Pageable pageable);
}
