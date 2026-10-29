
package com.hms.service;

import com.hms.dto.request.PaymentRequest;
import com.hms.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    Page<PaymentResponse> getPaymentsByBillId(
            Long billId,
            int page,
            int size
    );

    void deletePayment(Long id);
}
