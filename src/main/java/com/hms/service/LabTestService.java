
package com.hms.service;

import com.hms.dto.request.LabTestRequest;
import com.hms.dto.response.LabTestResponse;
import org.springframework.data.domain.Page;

public interface LabTestService {

    LabTestResponse createLabTest(LabTestRequest request);

    LabTestResponse getLabTestById(Long id);

    Page<LabTestResponse> getAllLabTests(int page, int size);

    LabTestResponse updateLabTest(Long id, LabTestRequest request);

    void deleteLabTest(Long id);
}
