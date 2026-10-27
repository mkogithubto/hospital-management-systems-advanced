
package com.hms.repository;

import com.hms.entity.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    Optional<LabTest> findByIdAndDeletedFalse(Long id);

    Optional<LabTest> findByTestCodeAndDeletedFalse(String testCode);

    boolean existsByTestCodeAndDeletedFalse(String testCode);

    Page<LabTest> findAllByDeletedFalse(Pageable pageable);
}
