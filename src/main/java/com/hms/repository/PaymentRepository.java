
package com.hms.repository;

import com.hms.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndDeletedFalse(Long id);

    Page<Payment> findAllByBillIdAndDeletedFalse(
            Long billId,
            Pageable pageable
    );

    boolean existsByPaymentReferenceAndDeletedFalse(
            String paymentReference
    );
}
