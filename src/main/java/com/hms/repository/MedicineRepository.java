
package com.hms.repository;

import com.hms.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByIdAndDeletedFalse(Long id);

    Optional<Medicine> findByMedicineCodeAndDeletedFalse(String medicineCode);

    boolean existsByMedicineCodeAndDeletedFalse(String medicineCode);

    Page<Medicine> findAllByDeletedFalse(Pageable pageable);
}
