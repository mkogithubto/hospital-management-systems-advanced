
package com.hms.service;

import com.hms.dto.request.BedRequest;
import com.hms.dto.response.BedResponse;
import org.springframework.data.domain.Page;

public interface BedService {

    BedResponse createBed(BedRequest request);

    BedResponse getBedById(Long id);

    Page<BedResponse> getAllBeds(int page, int size);

    BedResponse updateBed(Long id, BedRequest request);

    void deleteBed(Long id);
}
