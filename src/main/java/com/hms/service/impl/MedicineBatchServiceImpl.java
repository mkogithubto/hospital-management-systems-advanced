
package com.hms.service.impl;

import com.hms.dto.request.MedicineBatchRequest;
import com.hms.dto.response.MedicineBatchResponse;
import com.hms.entity.Medicine;
import com.hms.entity.MedicineBatch;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.MedicineBatchRepository;
import com.hms.repository.MedicineRepository;
import com.hms.service.MedicineBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.hms.dto.request.MedicineBatchUpdateRequest;
import java.time.LocalDate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MedicineBatchServiceImpl implements MedicineBatchService {

    private final MedicineBatchRepository medicineBatchRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public MedicineBatchResponse createMedicineBatch(MedicineBatchRequest request) {

        Medicine medicine = medicineRepository
                .findByIdAndDeletedFalse(request.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine with id " + request.getMedicineId() + " not found"
                ));

        if (medicineBatchRepository.existsByMedicineIdAndBatchNumberAndDeletedFalse(
                request.getMedicineId(), request.getBatchNumber())) {
            throw new DuplicateResourceException(
                    "Batch " + request.getBatchNumber()
                            + " already exists for this medicine"
            );
        }

        if (request.getManufacturingDate() != null
                && !request.getExpiryDate().isAfter(request.getManufacturingDate())) {
            throw new IllegalArgumentException(
                    "Expiry date must be after manufacturing date"
            );
        }

        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Expiry date cannot be in the past"
            );
        }

        MedicineBatch batch = new MedicineBatch();
        batch.setMedicine(medicine);
        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufacturingDate(request.getManufacturingDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setQuantityReceived(request.getQuantityReceived());
        batch.setAvailableQuantity(request.getQuantityReceived());
        batch.setPurchasePrice(request.getPurchasePrice());
        batch.setDeleted(false);

        MedicineBatch savedBatch = medicineBatchRepository.save(batch);

        return mapToResponse(savedBatch);
    }


    @Override
    @Transactional(readOnly = true)
    public MedicineBatchResponse getMedicineBatchById(Long id) {

        MedicineBatch batch = medicineBatchRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine batch with id " + id + " not found"
                ));

        return mapToResponse(batch);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<MedicineBatchResponse> getAllMedicineBatches(int page, int size) {

        return medicineBatchRepository
                .findAllByDeletedFalse(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }


    @Override
    @Transactional
    public MedicineBatchResponse updateMedicineBatch(
            Long id,
            MedicineBatchUpdateRequest request) {

        MedicineBatch batch = medicineBatchRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine batch with id " + id + " not found"
                ));

        if (request.getManufacturingDate() != null
                && !request.getExpiryDate().isAfter(request.getManufacturingDate())) {
            throw new IllegalArgumentException(
                    "Expiry date must be after manufacturing date"
            );
        }

        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Expiry date cannot be in the past"
            );
        }

        boolean batchNumberChanged =
                !batch.getBatchNumber().equals(request.getBatchNumber());

        if (batchNumberChanged
                && medicineBatchRepository
                .existsByMedicineIdAndBatchNumberAndDeletedFalse(
                        batch.getMedicine().getId(),
                        request.getBatchNumber())) {
            throw new DuplicateResourceException(
                    "Batch " + request.getBatchNumber()
                            + " already exists for this medicine"
            );
        }

        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufacturingDate(request.getManufacturingDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setPurchasePrice(request.getPurchasePrice());

        return mapToResponse(medicineBatchRepository.save(batch));
    }


    @Override
    @Transactional
    public void deleteMedicineBatch(Long id) {

        MedicineBatch batch = medicineBatchRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine batch with id " + id + " not found"
                ));

        batch.setDeleted(true);

        medicineBatchRepository.save(batch);
    }





    private MedicineBatchResponse mapToResponse(MedicineBatch batch) {
        return MedicineBatchResponse.builder()
                .id(batch.getId())
                .medicineId(batch.getMedicine().getId())
                .medicineCode(batch.getMedicine().getMedicineCode())
                .medicineName(batch.getMedicine().getName())
                .batchNumber(batch.getBatchNumber())
                .manufacturingDate(batch.getManufacturingDate())
                .expiryDate(batch.getExpiryDate())
                .quantityReceived(batch.getQuantityReceived())
                .availableQuantity(batch.getAvailableQuantity())
                .purchasePrice(batch.getPurchasePrice())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }
}
