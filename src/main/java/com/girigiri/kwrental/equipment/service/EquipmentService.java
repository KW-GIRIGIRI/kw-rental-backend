package com.girigiri.kwrental.equipment.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(final EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional(readOnly = true)
    public EquipmentDetailResponse findById(final Long id) {
        final Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(EquipmentNotFoundException::new);
        return EquipmentDetailResponse.from(equipment);
    }

    @Transactional(readOnly = true)
    public Page<SimpleEquipmentWithRentalQuantityResponse> findEquipmentsWithRentalQuantityBy(final Pageable pageable,
                                                                                              @Nullable final EquipmentSearchCondition searchCondition) {
        return equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(), searchCondition.category())
                .map(SimpleEquipmentWithRentalQuantityResponse::from);
    }

    public Page<SimpleEquipmentResponse> findEquipments(final Pageable pageable,
                                                        final EquipmentSearchCondition searchCondition) {
        return equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(), searchCondition.category())
                .map(SimpleEquipmentResponse::from);
    }
}
