
package com.hms.service;

import com.hms.dto.request.IpdAdmissionRequest;
import com.hms.dto.response.IpdAdmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hms.dto.request.UpdateIpdAdmissionRequest;

public interface IpdAdmissionService {

    IpdAdmissionResponse createAdmission(IpdAdmissionRequest request);

    IpdAdmissionResponse getAdmissionById(Long id);

    Page<IpdAdmissionResponse> getAllAdmissions(Pageable pageable);

    IpdAdmissionResponse updateAdmission(
            Long id,
            UpdateIpdAdmissionRequest request);

    IpdAdmissionResponse dischargeAdmission(Long id);

    void deleteAdmission(Long id);
}
