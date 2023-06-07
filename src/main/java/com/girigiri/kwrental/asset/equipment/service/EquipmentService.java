package com.girigiri.kwrental.asset.equipment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.asset.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentException;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;

import jakarta.annotation.Nullable;

@Service
public class EquipmentService {

	private final EquipmentRepository equipmentRepository;
	private final SaveItemService saveitemService;
	private final ApplicationEventPublisher eventPublisher;
	private final RemainingQuantityService remainingQuantityService;
	private final AssetService assetService;

	public EquipmentService(final EquipmentRepository equipmentRepository, final SaveItemService SaveitemService,
		final ApplicationEventPublisher eventPublisher, final RemainingQuantityService remainingQuantityService,
		final AssetService assetService) {
		this.equipmentRepository = equipmentRepository;
		this.saveitemService = SaveitemService;
		this.eventPublisher = eventPublisher;
		this.remainingQuantityService = remainingQuantityService;
		this.assetService = assetService;
	}

	@Transactional(readOnly = true)
	public EquipmentDetailResponse findById(final Long id) {
		final Equipment equipment = equipmentRepository.findById(id)
			.orElseThrow(EquipmentNotFoundException::new);
		return EquipmentDetailResponse.from(equipment);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateExistsById(final Long id) {
		equipmentRepository.findById(id)
			.orElseThrow(EquipmentNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Page<SimpleEquipmentWithRentalQuantityResponse> findEquipmentsWithRentalQuantityBy(final Pageable pageable,
		@Nullable final EquipmentSearchCondition searchCondition) {
		final Page<Equipment> page = equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(),
			searchCondition.category());
		final LocalDate date = searchCondition.date() == null ? LocalDate.now() : searchCondition.date();
		final Map<Long, Integer> remainingQuantities = getRemainingQuantities(page.getContent(), date);
		return page.map(equipment -> SimpleEquipmentWithRentalQuantityResponse.from(equipment,
			remainingQuantities.get(equipment.getId())));
	}

	private Map<Long, Integer> getRemainingQuantities(final List<Equipment> equipments, final LocalDate date) {
		final List<Long> ids = equipments.stream()
			.map(Equipment::getId)
			.toList();
		return remainingQuantityService.getRemainingQuantityByEquipmentIdAndDate(ids, date);
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
		saveitemService.saveItems(equipment.getId(), addEquipmentWithItemsRequest.items());
		return equipment.getId();
	}

	private Equipment mapToEquipment(final AddEquipmentRequest addEquipmentRequest) {
		return Equipment.builder()
			.name(addEquipmentRequest.getModelName())
			.maker(addEquipmentRequest.getMaker())
			.imgUrl(addEquipmentRequest.getImgUrl())
			.purpose(addEquipmentRequest.getPurpose())
			.category(Category.from(addEquipmentRequest.getCategory()))
			.description(addEquipmentRequest.getDescription())
			.components(addEquipmentRequest.getComponents())
			.rentalPlace(addEquipmentRequest.getRentalPlace())
			.totalQuantity(addEquipmentRequest.getTotalQuantity())
			.rentableQuantity(addEquipmentRequest.getTotalQuantity())
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

		equipment.setName(updateEquipmentRequest.modelName());
		equipment.setComponents(updateEquipmentRequest.components());
		equipment.setCategory(Category.from(updateEquipmentRequest.category()));
		equipment.setMaker(updateEquipmentRequest.maker());
		equipment.setDescription(updateEquipmentRequest.description());
		equipment.setPurpose(updateEquipmentRequest.purpose());
		equipment.setImgUrl(updateEquipmentRequest.imgUrl());
		equipment.setRentalPlace(updateEquipmentRequest.rentalPlace());
		equipment.setMaxRentalDays(updateEquipmentRequest.maxRentalDays());
		equipment.setTotalQuantity(updateEquipmentRequest.totalQuantity());
		return EquipmentDetailResponse.from(equipment);
	}

	@Transactional(readOnly = true)
	public Equipment validateRentalDays(final Long id, final Integer rentalDays) {
		final Equipment equipment = equipmentRepository.findById(id)
			.orElseThrow(EquipmentNotFoundException::new);
		if (!equipment.canRentDaysFor(rentalDays)) {
			throw new EquipmentException("최대 대여일 보다 더 길게 대여할 수 없습니다.");
		}
		return equipment;
	}

	@Transactional(readOnly = true)
	public RemainQuantitiesPerDateResponse getRemainQuantitiesPerDate(final Long equipmentId, final LocalDate from,
		final LocalDate to) {
		final Equipment equipment = equipmentRepository.findById(equipmentId)
			.orElseThrow(EquipmentNotFoundException::new);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(equipmentId,
			from, to);
		return assetService.getReservableCountPerDate(reservedAmounts, equipment);
	}
}
