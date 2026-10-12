package com.hms.service.impl;

import com.hms.dto.request.DepartmentRequest;
import com.hms.dto.response.DepartmentResponse;
import com.hms.entity.Department;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DepartmentRepository;
import com.hms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(
            DepartmentRequest request) {

        if (departmentRepository.existsByNameAndDeletedFalse(
                request.getName())) {
            throw new DuplicateResourceException(
                    "Department with name "
                            + request.getName()
                            + " already exists"
            );
        }

        Department department = Department.builder()
                .departmentCode(generateDepartmentCode())
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        Department savedDepartment =
                departmentRepository.save(department);

        return mapToResponse(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {

        Department department =
                departmentRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        return mapToResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Department> departmentPage =
                departmentRepository.findAllByDeletedFalse(pageable);

        return departmentPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request) {

        Department department =
                departmentRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        if (!department.getName().equals(request.getName())
                && departmentRepository.existsByNameAndDeletedFalse(
                        request.getName())){

            throw new DuplicateResourceException(
                    "Department with name "
                            + request.getName()
                            + " already exists"
            );

        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setStatus(request.getStatus());

        Department updatedDepartment =
                departmentRepository.save(department);

        return mapToResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {

        Department department =
                departmentRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        department.setDeleted(true);

        departmentRepository.save(department);
    }

    private String generateDepartmentCode() {

        long count = departmentRepository.count();

        return String.format(
                "DEP-%06d",
                count + 1
        );
    }

    private DepartmentResponse mapToResponse(
            Department department) {

        return DepartmentResponse.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .name(department.getName())
                .description(department.getDescription())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}