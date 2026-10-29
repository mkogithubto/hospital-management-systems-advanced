
package com.hms.repository;

import com.hms.entity.BillItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    Optional<BillItem> findByIdAndDeletedFalse(Long id);

    Page<BillItem> findAllByBillIdAndDeletedFalse(
            Long billId,
            Pageable pageable
    );
}
