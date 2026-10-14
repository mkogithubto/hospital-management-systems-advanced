
package com.hms.repository;

import com.hms.entity.OpdVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OpdVisitRepository extends JpaRepository<OpdVisit, Long> {

    boolean existsByOpdVisitCode(String opdVisitCode);

    Optional<OpdVisit> findByIdAndDeletedFalse(Long id);

    Page<OpdVisit> findAllByDeletedFalse(Pageable pageable);
}
