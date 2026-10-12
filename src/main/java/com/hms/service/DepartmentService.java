package com.hms.service;

import com.hms.dto.request.DepartmentRequest;
import com.hms.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse getDepartmentById(Long id);

    Page<DepartmentResponse> getAllDepartments(int page, int size);

    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);

    void deleteDepartment(Long id);
}