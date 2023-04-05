package com.girigiri.kwrental.equipment.service;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import jakarta.annotation.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ItemService itemService;
    private final ApplicationEventPublisher eventPublisher;

    public EquipmentService(final EquipmentRepository equipmentRepository, final ItemService itemService,
                            final ApplicationEventPublisher eventPublisher) {
        this.equipmentRepository = equipmentRepository;
        this.itemService = itemService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public EquipmentDetailResponse findById(final Long id) {
        final Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(EquipmentNotFoundException::new);
        return EquipmentDetailResponse.from(equipment);
    }

    @Transactional(readOnly = true)
    // TODO: 2023/03/30 대여가능 갯수 로직 구현해야 함
    public Page<SimpleEquipmentWithRentalQuantityResponse> findEquipmentsWithRentalQuantityBy(final Pageable pageable,
                                                                                              @Nullable final EquipmentSearchCondition searchCondition) {
        return equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(), searchCondition.category())
                .map(SimpleEquipmentWithRentalQuantityResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<SimpleEquipmentResponse> findEquipments(final Pageable pageable,
                                                        final EquipmentSearchCondition searchCondition) {
        return equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(), searchCondition.category())
                .map(SimpleEquipmentResponse::from);
    }

    @Transactional
    public Long saveEquipment(final AddEquipmentWithItemsRequest addEquipmentWithItemsRequest) {
        final AddEquipmentRequest addEquipmentRequest = addEquipmentWithItemsRequest.equipment();
        final Equipment equipment = equipmentRepository.save(mapToEquipment(addEquipmentRequest));
        itemService.saveItems(equipment.getId(), addEquipmentWithItemsRequest.items());
        return equipment.getId();
    }

    private Equipment mapToEquipment(final AddEquipmentRequest addEquipmentRequest) {
        return Equipment.builder()
                .modelName(addEquipmentRequest.getModelName())
                .maker(addEquipmentRequest.getMaker())
                .imgUrl(addEquipmentRequest.getImgUrl())
                .purpose(addEquipmentRequest.getPurpose())
                .category(Category.from(addEquipmentRequest.getCategory()))
                .description(addEquipmentRequest.getDescription())
                .components(addEquipmentRequest.getComponents())
                .rentalPlace(addEquipmentRequest.getRentalPlace())
                .totalQuantity(addEquipmentRequest.getTotalQuantity())
                .maxRentalDays(addEquipmentRequest.getMaxRentalDays())
                .build();
    }

    @Transactional
    public void deleteEquipment(final Long id) {
        equipmentRepository.findById(id)
                .orElseThrow(EquipmentNotFoundException::new);
        equipmentRepository.deleteById(id);
        eventPublisher.publishEvent(new EquipmentDeleteEvent(this, id));
    }

    @Transactional
    public EquipmentDetailResponse update(final Long id, final UpdateEquipmentRequest updateEquipmentRequest) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(EquipmentNotFoundException::new);

        equipment.setModelName(updateEquipmentRequest.modelName());
        equipment.setComponents(updateEquipmentRequest.component());
        equipment.setCategory(Category.from(updateEquipmentRequest.category()));
        equipment.setMaker(updateEquipmentRequest.maker());
        equipment.setDescription(updateEquipmentRequest.description());
        equipment.setPurpose(updateEquipmentRequest.purpose());
        equipment.setImgUrl(updateEquipmentRequest.imgUrl());
        equipment.setRentalPlace(updateEquipmentRequest.rentalDays());
        equipment.setMaxRentalDays(updateEquipmentRequest.maxRentalDays());
        equipment.setTotalQuantity(updateEquipmentRequest.totalQuantity());
        return EquipmentDetailResponse.from(equipment);
    }

    @Transactional(readOnly = true)
    public void validateRentalDays(final Long id, final Integer rentalDays) {
        final Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(EquipmentNotFoundException::new);
        if (!equipment.canRentFor(rentalDays)) {
            throw new EquipmentException("최대 대여일 보다 더 길게 대여할 수 없습니다.");
        }
    }
}
