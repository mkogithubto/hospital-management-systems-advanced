
package com.hms.repository;

import com.hms.entity.IpdAdmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpdAdmissionRepository
        extends JpaRepository<IpdAdmission, Long> {

    boolean existsByAdmissionCode(String admissionCode);

    Optional<IpdAdmission> findByIdAndDeletedFalse(Long id);

    Page<IpdAdmission> findAllByDeletedFalse(Pageable pageable);
}
