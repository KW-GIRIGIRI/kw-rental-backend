package com.girigiri.kwrental.labroom.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.labroom.exception.LabRoomNotFoundException;
import com.girigiri.kwrental.labroom.repository.LabRoomRepository;

@Service
public class LabRoomService {
	private final RemainingQuantityService remainingQuantityService;
	private final LabRoomRepository labRoomRepository;
	private final AssetService assetService;

	public LabRoomService(final RemainingQuantityService remainingQuantityService,
		final LabRoomRepository labRoomRepository, final AssetService assetService) {
		this.remainingQuantityService = remainingQuantityService;
		this.labRoomRepository = labRoomRepository;
		this.assetService = assetService;
	}

	@Transactional(readOnly = true)
	public RemainQuantitiesPerDateResponse getRemainQuantityByLabRoomName(final String name, final LocalDate from,
		final LocalDate to) {
		final LabRoom labRoom = labRoomRepository.findLabRoomByName(name)
			.orElseThrow(LabRoomNotFoundException::new);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(
			labRoom.getId(), from, to);
		return assetService.getReservableCountPerDate(reservedAmounts, labRoom);
	}

	@Transactional(readOnly = true)
	public RemainReservationCountsPerDateResponse getLabRoomRemainReservationCountPerDateResponse(final String name,
		final LocalDate from, final LocalDate to) {
		final LabRoom labRoom = labRoomRepository.findLabRoomByName(name)
			.orElseThrow(LabRoomNotFoundException::new);
		Map<LocalDate, Integer> reservationCounts = remainingQuantityService.getReservationCountInclusive(
			labRoom.getId(), from, to);
		List<RemainReservationCountPerDateResponse> remainReservationCountPerDateResponses = reservationCounts.keySet()
			.stream()
			.map(date -> new RemainReservationCountPerDateResponse(date,
				labRoom.getReservationCountPerDay() - reservationCounts.get(date)))
			.sorted(Comparator.comparing(RemainReservationCountPerDateResponse::getDate))
			.toList();
		return new RemainReservationCountsPerDateResponse(labRoom.getId(), remainReservationCountPerDateResponses);
	}
}
