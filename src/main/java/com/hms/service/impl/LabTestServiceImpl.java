
package com.hms.service.impl;

import com.hms.dto.request.LabTestRequest;
import com.hms.dto.response.LabTestResponse;
import com.hms.entity.LabTest;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.LabTestRepository;
import com.hms.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;

    @Override
    public LabTestResponse createLabTest(LabTestRequest request) {

        if (labTestRepository.existsByTestCodeAndDeletedFalse(request.getTestCode())) {
            throw new DuplicateResourceException(
                    "Lab test code already exists: " + request.getTestCode()
            );
        }

        LabTest labTest = LabTest.builder()
                .testCode(request.getTestCode())
                .testName(request.getTestName())
                .category(request.getCategory())
                .description(request.getDescription())
                .sampleType(request.getSampleType())
                .price(request.getPrice())
                .status("ACTIVE")
                .deleted(false)
                .build();

        return mapToResponse(labTestRepository.save(labTest));
    }

    @Override
    @Transactional(readOnly = true)
    public LabTestResponse getLabTestById(Long id) {

        LabTest labTest = labTestRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab test not found with id: " + id
                ));

        return mapToResponse(labTest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabTestResponse> getAllLabTests(int page, int size) {

        return labTestRepository.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Override
    public LabTestResponse updateLabTest(Long id, LabTestRequest request) {

        LabTest labTest = labTestRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab test not found with id: " + id
                ));

        boolean codeUsedByAnotherTest =
                labTestRepository.existsByTestCodeAndDeletedFalse(request.getTestCode())
                        && !labTest.getTestCode().equals(request.getTestCode());

        if (codeUsedByAnotherTest) {
            throw new DuplicateResourceException(
                    "Lab test code already exists: " + request.getTestCode()
            );
        }

        labTest.setTestCode(request.getTestCode());
        labTest.setTestName(request.getTestName());
        labTest.setCategory(request.getCategory());
        labTest.setDescription(request.getDescription());
        labTest.setSampleType(request.getSampleType());
        labTest.setPrice(request.getPrice());

        return mapToResponse(labTestRepository.save(labTest));
    }

    @Override
    public void deleteLabTest(Long id) {

        LabTest labTest = labTestRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab test not found with id: " + id
                ));

        labTest.setDeleted(true);
        labTestRepository.save(labTest);
    }

    private LabTestResponse mapToResponse(LabTest labTest) {

        return LabTestResponse.builder()
                .id(labTest.getId())
                .testCode(labTest.getTestCode())
                .testName(labTest.getTestName())
                .category(labTest.getCategory())
                .description(labTest.getDescription())
                .sampleType(labTest.getSampleType())
                .price(labTest.getPrice())
                .status(labTest.getStatus())
                .createdAt(labTest.getCreatedAt())
                .updatedAt(labTest.getUpdatedAt())
                .build();
    }
}
