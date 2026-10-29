package com.hms.repository;

import com.hms.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByIdAndDeletedFalse(Long id);

    Optional<Bill> findByBillNumberAndDeletedFalse(String billNumber);

    boolean existsByBillNumberAndDeletedFalse(String billNumber);

    Page<Bill> findAllByDeletedFalse(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bill b WHERE b.id = :id AND b.deleted = false")
    Optional<Bill> findByIdAndDeletedFalseForUpdate(@Param("id") Long id);
}