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
import com.girigiri.kwrental.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.labroom.dto.response.LabRoomNoticeResponse;
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

	private static List<RemainReservationCountPerDateResponse> getRemainReservationCountPerDateResponses(
		LabRoom labRoom, Map<LocalDate, Integer> reservationCounts) {
		return reservationCounts.keySet()
			.stream()
			.map(date -> new RemainReservationCountPerDateResponse(date,
				labRoom.getReservationCountPerDay() - reservationCounts.get(date)))
			.sorted(Comparator.comparing(RemainReservationCountPerDateResponse::getDate))
			.toList();
	}

	@Transactional(readOnly = true)
	public RemainQuantitiesPerDateResponse getRemainQuantityByLabRoomName(final String name, final LocalDate from,
		final LocalDate to) {
		final LabRoom labRoom = getLabRoom(name);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(
			labRoom.getId(), from, to);
		return assetService.getReservableCountPerDate(reservedAmounts, labRoom);
	}

	private LabRoom getLabRoom(String name) {
		return labRoomRepository.findLabRoomByName(name)
			.orElseThrow(LabRoomNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public RemainReservationCountsPerDateResponse getRemainReservationCountByLabRoomName(final String name,
		final LocalDate from, final LocalDate to) {
		final LabRoom labRoom = getLabRoom(name);
		Map<LocalDate, Integer> reservationCounts = remainingQuantityService.getReservationCountInclusive(
			labRoom.getId(), from, to);
		List<RemainReservationCountPerDateResponse> remainReservationCountPerDateResponses = getRemainReservationCountPerDateResponses(
			labRoom, reservationCounts);
		return new RemainReservationCountsPerDateResponse(labRoom.getId(), remainReservationCountPerDateResponses);
	}

	@Transactional
	public void setNotice(String name, LabRoomNoticeRequest labRoomNoticeRequest) {
		LabRoom labRoom = getLabRoom(name);
		labRoomRepository.updateNotice(labRoom.getId(), labRoomNoticeRequest.getNotice());
	}

	public LabRoomNoticeResponse getNotice(String name) {
		LabRoom labRoom = getLabRoom(name);
		return new LabRoomNoticeResponse(labRoom.getNotice());
	}
}
