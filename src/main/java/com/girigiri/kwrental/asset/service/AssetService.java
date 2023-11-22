package com.girigiri.kwrental.asset.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.exception.AssetNotFoundException;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.operation.service.OperationChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class AssetService {

	private final AssetRepository assetRepository;
	private final OperationChecker operationChecker;

	public RemainQuantitiesPerDateResponse getReservableCountPerDate(final Map<LocalDate, Integer> reservedAmounts,
		final RentableAsset asset) {
		final Set<LocalDate> operateDates = Set.copyOf(
			operationChecker.getOperateDates(List.copyOf(reservedAmounts.keySet())));
		final List<RemainQuantityPerDateResponse> remainQuantityPerDateResponses = reservedAmounts.keySet().stream()
			.map(date -> mapToRemainQuantityPerDateResponse(reservedAmounts, operateDates, asset, date))
			.sorted(Comparator.comparing(RemainQuantityPerDateResponse::date))
			.toList();
		return new RemainQuantitiesPerDateResponse(remainQuantityPerDateResponses);
	}

	private RemainQuantityPerDateResponse mapToRemainQuantityPerDateResponse(
		final Map<LocalDate, Integer> reservedAmounts, final Set<LocalDate> operateDates, final RentableAsset asset,
		final LocalDate date) {
		if (!operateDates.contains(date))
			return new RemainQuantityPerDateResponse(date, 0);
		final int remainQuantity = asset.getRemainQuantity(reservedAmounts.get(date));
		return new RemainQuantityPerDateResponse(date, remainQuantity);
	}

	public RentableAsset getAssetById(final Long id) {
		return assetRepository.findById(id)
			.orElseThrow(AssetNotFoundException::new);
	}
}
