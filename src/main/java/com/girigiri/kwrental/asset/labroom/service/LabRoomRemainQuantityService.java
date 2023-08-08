package com.girigiri.kwrental.asset.labroom.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabRoomRemainQuantityService {

	private final LabRoomDailyBanRetriever labRoomDailyBanRetriever;
	private final LabRoomRetriever labRoomRetriever;
	private final RemainingQuantityService remainingQuantityService;
	private final AssetService assetService;

	public RemainQuantitiesPerDateResponse getRemainQuantityByLabRoomName(final String name, final LocalDate from,
		final LocalDate to) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(
			labRoom.getId(), from, to);
		final RemainQuantitiesPerDateResponse remainQuantitiesPerDateResponse = assetService.getReservableCountPerDate(
			reservedAmounts, labRoom);
		final Map<LocalDate, LabRoomDailyBan> bans = labRoomDailyBanRetriever.getLabRoomBanByDates(labRoom.getId(),
			from,
			to);
		return adjustBans(remainQuantitiesPerDateResponse.getRemainQuantities(), bans);
	}

	private RemainQuantitiesPerDateResponse adjustBans(
		final List<RemainQuantityPerDateResponse> remainQuantities,
		final Map<LocalDate, LabRoomDailyBan> bans) {
		final List<RemainQuantityPerDateResponse> adjustedBansRemainQuantities = remainQuantities.stream()
			.map(it -> adjustBan(it, bans))
			.toList();
		return new RemainQuantitiesPerDateResponse(adjustedBansRemainQuantities);
	}

	private RemainQuantityPerDateResponse adjustBan(final RemainQuantityPerDateResponse remainQuantityPerDateResponse,
		final Map<LocalDate, LabRoomDailyBan> bans) {
		final LocalDate date = remainQuantityPerDateResponse.date();
		if (bans.get(date) != null) {
			return remainQuantityPerDateResponse.createEmptyQuanittyWithSameDate();
		}
		return remainQuantityPerDateResponse;
	}

	public RemainReservationCountsPerDateResponse getRemainReservationCountByLabRoomName(final String name,
		final LocalDate from, final LocalDate to) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		final Map<LocalDate, Integer> reservationCounts = remainingQuantityService.getReservationCountInclusive(
			labRoom.getId(), from, to);
		Map<LocalDate, LabRoomDailyBan> bans = labRoomDailyBanRetriever.getLabRoomBanByDates(labRoom.getId(), from, to);
		final List<RemainReservationCountPerDateResponse> remainReservationCountPerDateResponses = getRemainReservationCountPerDateResponses(
			labRoom, reservationCounts, bans);
		return new RemainReservationCountsPerDateResponse(labRoom.getId(), remainReservationCountPerDateResponses);
	}

	private List<RemainReservationCountPerDateResponse> getRemainReservationCountPerDateResponses(
		final LabRoom labRoom, final Map<LocalDate, Integer> reservationCounts,
		final Map<LocalDate, LabRoomDailyBan> bans) {
		return reservationCounts.keySet()
			.stream()
			.map(date -> createRemainReservationCountPerDateResponse(date, labRoom.getRemainReservationCount(
				reservationCounts.get(date)), bans.get(date)))
			.sorted(Comparator.comparing(RemainReservationCountPerDateResponse::getDate))
			.toList();
	}

	private RemainReservationCountPerDateResponse createRemainReservationCountPerDateResponse(final LocalDate date,
		final Integer remainReservationCount, final LabRoomDailyBan ban) {
		if (ban != null) {
			return new RemainReservationCountPerDateResponse(date, 0);
		}
		return new RemainReservationCountPerDateResponse(date, remainReservationCount);
	}
}

