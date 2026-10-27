
package com.hms.repository;

import com.hms.entity.LabOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {

    Optional<LabOrder> findByIdAndDeletedFalse(Long id);

    Optional<LabOrder> findByOrderCodeAndDeletedFalse(String orderCode);

    boolean existsByOrderCodeAndDeletedFalse(String orderCode);

    Page<LabOrder> findAllByDeletedFalse(Pageable pageable);
}
