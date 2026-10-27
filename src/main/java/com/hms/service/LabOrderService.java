
package com.hms.service;

import com.hms.dto.request.LabOrderRequest;
import com.hms.dto.response.LabOrderResponse;
import org.springframework.data.domain.Page;

public interface LabOrderService {

    LabOrderResponse createLabOrder(LabOrderRequest request);

    LabOrderResponse getLabOrderById(Long id);

    Page<LabOrderResponse> getAllLabOrders(int page, int size);

    void deleteLabOrder(Long id);
}
