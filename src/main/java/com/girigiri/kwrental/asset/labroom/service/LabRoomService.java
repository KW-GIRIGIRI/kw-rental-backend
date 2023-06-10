package com.girigiri.kwrental.asset.labroom.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomAvailableDateFailureException;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotFoundException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;

@Service
public class LabRoomService {
	private final RemainingQuantityService remainingQuantityService;
	private final LabRoomRepository labRoomRepository;
	private final AssetService assetService;
	private final LabRoomDailyBanRepository labRoomDailyBanRepository;

	public LabRoomService(final RemainingQuantityService remainingQuantityService,
		final LabRoomRepository labRoomRepository, final AssetService assetService,
		LabRoomDailyBanRepository labRoomDailyBanRepository) {
		this.remainingQuantityService = remainingQuantityService;
		this.labRoomRepository = labRoomRepository;
		this.assetService = assetService;
		this.labRoomDailyBanRepository = labRoomDailyBanRepository;
	}

	@Transactional(readOnly = true)
	public RemainQuantitiesPerDateResponse getRemainQuantityByLabRoomName(final String name, final LocalDate from,
		final LocalDate to) {
		final LabRoom labRoom = getLabRoom(name);
		final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountInclusive(
			labRoom.getId(), from, to);
		final RemainQuantitiesPerDateResponse remainQuantitiesPerDateResponse = assetService.getReservableCountPerDate(
			reservedAmounts, labRoom);
		final List<LabRoomDailyBan> bans = labRoomDailyBanRepository.findByLabRoomIdAndInclusive(labRoom.getId(), from,
			to);
		return adjustBans(remainQuantitiesPerDateResponse, bans);
	}

	private RemainQuantitiesPerDateResponse adjustBans(
		final RemainQuantitiesPerDateResponse remainQuantitiesPerDateResponse, final List<LabRoomDailyBan> bans) {
		final Map<LocalDate, LabRoomDailyBan> banMap = bans.stream()
			.collect(Collectors.toMap(LabRoomDailyBan::getBanDate, Function.identity()));
		for (final RemainQuantityPerDateResponse remainQuantityPerDateResponse : remainQuantitiesPerDateResponse.getRemainQuantities()) {
			final LocalDate date = remainQuantityPerDateResponse.getDate();
			if (banMap.get(date) != null) {
				remainQuantityPerDateResponse.setRemainQuantity(0);
			}
		}
		return remainQuantitiesPerDateResponse;
	}

	private LabRoom getLabRoom(String name) {
		return labRoomRepository.findLabRoomByName(name)
			.orElseThrow(LabRoomNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public RemainReservationCountsPerDateResponse getRemainReservationCountByLabRoomName(final String name,
		final LocalDate from, final LocalDate to) {
		final LabRoom labRoom = getLabRoom(name);
		final Map<LocalDate, Integer> reservationCounts = remainingQuantityService.getReservationCountInclusive(
			labRoom.getId(), from, to);
		final List<LabRoomDailyBan> bans = labRoomDailyBanRepository.findByLabRoomIdAndInclusive(
			labRoom.getId(), from, to);
		final List<RemainReservationCountPerDateResponse> remainReservationCountPerDateResponses = getRemainReservationCountPerDateResponses(
			labRoom, reservationCounts, bans);
		return new RemainReservationCountsPerDateResponse(labRoom.getId(), remainReservationCountPerDateResponses);
	}

	private List<RemainReservationCountPerDateResponse> getRemainReservationCountPerDateResponses(
		final LabRoom labRoom, final Map<LocalDate, Integer> reservationCounts, final List<LabRoomDailyBan> bans) {
		final Map<LocalDate, LabRoomDailyBan> banMap = bans.stream()
			.collect(Collectors.toMap(LabRoomDailyBan::getBanDate, Function.identity()));
		return reservationCounts.keySet()
			.stream()
			.map(date -> createRemainReservationCountPerDateResponse(date, labRoom.getRemainReservationCount(
				reservationCounts.get(date)), banMap.get(date)))
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

	@Transactional
	public void setNotice(String name, LabRoomNoticeRequest labRoomNoticeRequest) {
		final LabRoom labRoom = getLabRoom(name);
		labRoomRepository.updateNotice(labRoom.getId(), labRoomNoticeRequest.getNotice());
	}

	@Transactional(readOnly = true)
	public LabRoomNoticeResponse getNotice(String name) {
		final LabRoom labRoom = getLabRoom(name);
		return new LabRoomNoticeResponse(labRoom.getNotice());
	}

	@Transactional
	public void setAvailableForEntirePeriod(String name, boolean available) {
		final LabRoom labRoom = getLabRoom(name);
		labRoomRepository.updateAvailable(labRoom.getId(), available);
	}

	@Transactional
	public void setAvailable(final String name, final LocalDate date, final boolean available) {
		final LabRoom labRoom = getLabRoom(name);
		labRoomDailyBanRepository.findByLabRoomIdAndBanDate(labRoom.getId(), date)
			.ifPresentOrElse(labRoomDailyBan -> makeAvailable(labRoom, labRoomDailyBan, available),
				() -> makeUnavailable(labRoom, available, date));
	}

	private void makeAvailable(LabRoom labRoom, final LabRoomDailyBan labRoomDailyBan, final boolean available) {
		if (!labRoom.isAvailable()) {
			throw new LabRoomAvailableDateFailureException();
		}
		if (available) {
			labRoomDailyBanRepository.deleteById(labRoomDailyBan.getId());
		}
	}

	private void makeUnavailable(final LabRoom labRoom, final boolean available, final LocalDate date) {
		if (!available) {
			labRoomDailyBanRepository.save(LabRoomDailyBan.builder().labRoomId(labRoom.getId()).banDate(date).build());
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateDays(final Rentable rentable, final Set<LocalDate> rentalDays) {
		List<LabRoomDailyBan> bans = labRoomDailyBanRepository.findByLabRoomId(rentable.getId());
		boolean isBanned = bans.stream()
			.anyMatch(ban -> ban.hasAny(rentalDays));
		if (isBanned)
			throw new LabRoomNotAvailableException();
	}

	@Transactional(readOnly = true)
	public LabRoomAvailableResponse getAvailableByDate(final String name, final LocalDate date) {
		LabRoom labRoom = getLabRoom(name);
		boolean availableByDate = labRoomDailyBanRepository.findByLabRoomIdAndBanDate(labRoom.getId(), date)
			.isEmpty() && labRoom.isAvailable();
		return new LabRoomAvailableResponse(labRoom.getId(), availableByDate, date);
	}

	@Transactional(readOnly = true)
	public LabRoomAvailableResponse getAvailable(final String name) {
		final LabRoom labRoom = getLabRoom(name);
		final boolean available = labRoom.isAvailable();
		return new LabRoomAvailableResponse(labRoom.getId(), available, null);
	}
}
