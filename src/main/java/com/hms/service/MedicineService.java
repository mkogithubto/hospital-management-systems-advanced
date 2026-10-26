
package com.hms.service;

import com.hms.dto.request.MedicineRequest;
import com.hms.dto.response.MedicineResponse;
import org.springframework.data.domain.Page;

public interface MedicineService {

    MedicineResponse createMedicine(MedicineRequest request);

    MedicineResponse getMedicineById(Long id);

    Page<MedicineResponse> getAllMedicines(int page, int size);

    MedicineResponse updateMedicine(Long id, MedicineRequest request);

    void deleteMedicine(Long id);
}
