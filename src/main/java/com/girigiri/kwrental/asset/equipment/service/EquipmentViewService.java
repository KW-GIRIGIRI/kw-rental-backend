package com.girigiri.kwrental.asset.equipment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.asset.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentViewService {

	private final AssetService assetService;
	private final EquipmentRetriever equipmentRetriever;
	private final EquipmentRepository equipmentRepository;
	private final RemainingQuantityService remainingQuantityService;

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
		return remainingQuantityService.getRemainingQuantityByAssetIdAndDate(ids, date);
	}

	public Page<SimpleEquipmentResponse> findEquipments(final Pageable pageable,
		final EquipmentSearchCondition searchCondition) {
		return equipmentRepository.findEquipmentBy(pageable, searchCondition.keyword(), searchCondition.category())
			.map(SimpleEquipmentResponse::from);
	}

	public EquipmentDetailResponse findById(final Long id) {
		final Equipment equipment = equipmentRetriever.getEquipment(id);
		return EquipmentDetailResponse.from(equipment);
	}

	public RemainQuantitiesPerDateResponse getRemainQuantitiesPerDate(final Long equipmentId, final LocalDate from,
		final LocalDate to) {
		final Equipment equipment = equipmentRetriever.getEquipment(equipmentId);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(equipmentId,
			from, to);
		return assetService.getReservableCountPerDate(reservedAmounts, equipment);
	}
}
