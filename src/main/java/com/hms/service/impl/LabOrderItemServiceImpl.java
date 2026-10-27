
package com.hms.service.impl;

import com.hms.dto.request.LabOrderItemRequest;
import com.hms.dto.response.LabOrderItemResponse;
import com.hms.entity.LabOrder;
import com.hms.entity.LabOrderItem;
import com.hms.entity.LabTest;
import com.hms.exception.DuplicateResourceException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.LabOrderItemRepository;
import com.hms.repository.LabOrderRepository;
import com.hms.repository.LabTestRepository;
import com.hms.service.LabOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabOrderItemServiceImpl implements LabOrderItemService {

    private final LabOrderItemRepository labOrderItemRepository;
    private final LabOrderRepository labOrderRepository;
    private final LabTestRepository labTestRepository;

    @Override
    public LabOrderItemResponse addLabOrderItem(LabOrderItemRequest request) {

        LabOrder labOrder = labOrderRepository
                .findByIdAndDeletedFalse(request.getLabOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order not found with id: " + request.getLabOrderId()
                ));

        LabTest labTest = labTestRepository
                .findByIdAndDeletedFalse(request.getLabTestId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab test not found with id: " + request.getLabTestId()
                ));

        if (labOrderItemRepository
                .existsByLabOrderIdAndLabTestIdAndDeletedFalse(
                        request.getLabOrderId(),
                        request.getLabTestId())) {
            throw new DuplicateResourceException(
                    "This lab test is already added to the lab order"
            );
        }

        LabOrderItem item = LabOrderItem.builder()
                .labOrder(labOrder)
                .labTest(labTest)
                .priceAtOrder(labTest.getPrice())
                .status("ORDERED")
                .deleted(false)
                .build();

        return mapToResponse(labOrderItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public LabOrderItemResponse getLabOrderItemById(Long id) {

        LabOrderItem item = labOrderItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order item not found with id: " + id
                ));

        return mapToResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabOrderItemResponse> getLabOrderItemsByLabOrderId(
            Long labOrderId,
            int page,
            int size) {

        labOrderRepository.findByIdAndDeletedFalse(labOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order not found with id: " + labOrderId
                ));

        return labOrderItemRepository
                .findAllByLabOrderIdAndDeletedFalse(
                        labOrderId,
                        PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Override
    public void deleteLabOrderItem(Long id) {

        LabOrderItem item = labOrderItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab order item not found with id: " + id
                ));

        item.setDeleted(true);
        labOrderItemRepository.save(item);
    }

    private LabOrderItemResponse mapToResponse(LabOrderItem item) {

        return LabOrderItemResponse.builder()
                .id(item.getId())
                .labOrderId(item.getLabOrder().getId())
                .orderCode(item.getLabOrder().getOrderCode())
                .labTestId(item.getLabTest().getId())
                .testCode(item.getLabTest().getTestCode())
                .testName(item.getLabTest().getTestName())
                .priceAtOrder(item.getPriceAtOrder())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
