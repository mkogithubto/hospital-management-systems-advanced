
package com.hms.service;

import com.hms.dto.request.OpdVisitRequest;
import com.hms.dto.response.OpdVisitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hms.dto.request.UpdateOpdVisitRequest;

public interface OpdVisitService {

    OpdVisitResponse createOpdVisit(OpdVisitRequest request);

    OpdVisitResponse getOpdVisitById(Long id);

    Page<OpdVisitResponse> getAllOpdVisits(Pageable pageable);

    OpdVisitResponse updateOpdVisit(Long id, UpdateOpdVisitRequest request);

    void deleteOpdVisit(Long id);

}
