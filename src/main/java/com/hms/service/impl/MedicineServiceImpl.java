
package com.hms.service.impl;

import com.hms.dto.request.MedicineRequest;
import com.hms.dto.response.MedicineResponse;
import com.hms.entity.Medicine;
import com.hms.exception.DuplicateResourceException;
import com.hms.repository.MedicineRepository;
import com.hms.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hms.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public MedicineResponse createMedicine(MedicineRequest request) {

        if (medicineRepository.existsByMedicineCodeAndDeletedFalse(
                request.getMedicineCode())) {

            throw new DuplicateResourceException(
                    "Medicine with code "
                            + request.getMedicineCode()
                            + " already exists"
            );
        }

        Medicine medicine = Medicine.builder()
                .medicineCode(request.getMedicineCode())
                .name(request.getName())
                .genericName(request.getGenericName())
                .manufacturer(request.getManufacturer())
                .category(request.getCategory())
                .strength(request.getStrength())
                .dosageForm(request.getDosageForm())
                .prescriptionRequired(request.isPrescriptionRequired())
                .status("ACTIVE")
                .build();

        Medicine savedMedicine = medicineRepository.save(medicine);

        return mapToResponse(savedMedicine);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {

        Medicine medicine = medicineRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medicine with id " + id + " not found"
                        )
                );

        return mapToResponse(medicine);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicineResponse> getAllMedicines(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Medicine> medicinePage =
                medicineRepository.findAllByDeletedFalse(pageable);

        return medicinePage.map(this::mapToResponse);
    }


    @Override
    @Transactional
    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request) {

        Medicine medicine = medicineRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medicine with id " + id + " not found"
                        )
                );

        if (!medicine.getMedicineCode().equals(request.getMedicineCode())
                && medicineRepository.existsByMedicineCodeAndDeletedFalse(
                request.getMedicineCode())) {

            throw new DuplicateResourceException(
                    "Medicine with code "
                            + request.getMedicineCode()
                            + " already exists"
            );
        }

        medicine.setMedicineCode(request.getMedicineCode());
        medicine.setName(request.getName());
        medicine.setGenericName(request.getGenericName());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setCategory(request.getCategory());
        medicine.setStrength(request.getStrength());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setPrescriptionRequired(request.isPrescriptionRequired());

        Medicine updatedMedicine = medicineRepository.save(medicine);

        return mapToResponse(updatedMedicine);
    }


    @Override
    @Transactional
    public void deleteMedicine(Long id) {

        Medicine medicine = medicineRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medicine with id " + id + " not found"
                        )
                );

        medicine.setDeleted(true);

        medicineRepository.save(medicine);
    }



    private MedicineResponse mapToResponse(Medicine medicine) {

        return MedicineResponse.builder()
                .id(medicine.getId())
                .medicineCode(medicine.getMedicineCode())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .manufacturer(medicine.getManufacturer())
                .category(medicine.getCategory())
                .strength(medicine.getStrength())
                .dosageForm(medicine.getDosageForm())
                .prescriptionRequired(medicine.isPrescriptionRequired())
                .status(medicine.getStatus())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
