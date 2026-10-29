
package com.hms.service;

import com.hms.dto.request.BillItemRequest;
import com.hms.dto.response.BillItemResponse;
import org.springframework.data.domain.Page;

public interface BillItemService {

    BillItemResponse createBillItem(BillItemRequest request);

    BillItemResponse getBillItemById(Long id);

    Page<BillItemResponse> getBillItemsByBillId(
            Long billId,
            int page,
            int size
    );

    void deleteBillItem(Long id);
}
