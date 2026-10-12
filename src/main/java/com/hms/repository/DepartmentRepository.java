package com.hms.repository;

import com.hms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentCodeAndDeletedFalse(
            String departmentCode);

    Optional<Department> findByNameAndDeletedFalse(
            String name);

    Optional<Department> findByIdAndDeletedFalse(Long id);

    boolean existsByNameAndDeletedFalse(String name);

    boolean existsByDepartmentCodeAndDeletedFalse(
            String departmentCode);

    Page<Department> findAllByDeletedFalse(Pageable pageable);
}