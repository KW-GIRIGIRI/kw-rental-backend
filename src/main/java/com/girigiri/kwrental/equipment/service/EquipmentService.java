package com.girigiri.kwrental.equipment.service;

import com.girigiri.kwrental.equipment.EquipmentRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    public Slice<EquipmentResponse> findEquipmentsBy(final Pageable pageable) {
        return equipmentRepository.findEquipmentsBy(pageable)
                .map(EquipmentResponse::from);
    }
}
