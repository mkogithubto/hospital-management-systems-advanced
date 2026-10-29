
package com.hms.service;

import com.hms.dto.request.BillRequest;
import com.hms.dto.response.BillResponse;
import org.springframework.data.domain.Page;

public interface BillService {

    BillResponse createBill(BillRequest request);

    BillResponse getBillById(Long id);

    Page<BillResponse> getAllBills(int page, int size);

    void deleteBill(Long id);
}
