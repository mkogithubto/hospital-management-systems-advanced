
package com.hms.service;

import com.hms.dto.request.MedicineBatchRequest;
import com.hms.dto.response.MedicineBatchResponse;
import org.springframework.data.domain.Page;
import com.hms.dto.request.MedicineBatchUpdateRequest;

public interface MedicineBatchService {

    MedicineBatchResponse createMedicineBatch(MedicineBatchRequest request);

    MedicineBatchResponse getMedicineBatchById(Long id);

    Page<MedicineBatchResponse> getAllMedicineBatches(int page, int size);

    MedicineBatchResponse updateMedicineBatch(
            Long id,
            MedicineBatchUpdateRequest request
    );

    void deleteMedicineBatch(Long id);
}
