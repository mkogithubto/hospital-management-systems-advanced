
package com.hms.service;

import com.hms.dto.request.LabOrderItemRequest;
import com.hms.dto.response.LabOrderItemResponse;
import org.springframework.data.domain.Page;

public interface LabOrderItemService {

    LabOrderItemResponse addLabOrderItem(LabOrderItemRequest request);

    LabOrderItemResponse getLabOrderItemById(Long id);

    Page<LabOrderItemResponse> getLabOrderItemsByLabOrderId(
            Long labOrderId,
            int page,
            int size
    );

    void deleteLabOrderItem(Long id);
}
