
package com.hms.repository;

import com.hms.entity.LabOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabOrderItemRepository
        extends JpaRepository<LabOrderItem, Long> {

    Optional<LabOrderItem> findByIdAndDeletedFalse(Long id);

    Page<LabOrderItem> findAllByLabOrderIdAndDeletedFalse(
            Long labOrderId,
            Pageable pageable
    );

    boolean existsByLabOrderIdAndLabTestIdAndDeletedFalse(
            Long labOrderId,
            Long labTestId
    );
}
