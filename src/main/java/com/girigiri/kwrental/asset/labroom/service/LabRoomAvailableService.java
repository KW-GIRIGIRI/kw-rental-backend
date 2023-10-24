package com.girigiri.kwrental.asset.labroom.service;

import java.time.LocalDate;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomAvailableDateFailureException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.asset.labroom.service.event.LabRoomDailyUnavailableEvent;
import com.girigiri.kwrental.asset.labroom.service.event.LabRoomUnavailableEvent;

import lombok.RequiredArgsConstructor;

@Service

@Transactional
@RequiredArgsConstructor
public class LabRoomAvailableService {
	private final LabRoomRetriever labRoomRetriever;
	private final LabRoomRepository labRoomRepository;
	private final LabRoomDailyBanRepository labRoomDailyBanRepository;
	private final ApplicationEventPublisher eventPublisher;

	public void setAvailableForEntirePeriod(final String name, boolean available) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		if (!available)
			eventPublisher.publishEvent(new LabRoomUnavailableEvent(this, labRoom));
		labRoomRepository.updateAvailable(labRoom.getId(), available);
	}

	public void setAvailable(final String name, final LocalDate date, final boolean available) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		labRoomDailyBanRepository.findByLabRoomIdAndBanDate(labRoom.getId(), date)
			.ifPresentOrElse(labRoomDailyBan -> makeAvailable(labRoom, labRoomDailyBan, available),
				() -> makeUnavailable(labRoom, available, date));
	}

	private void makeAvailable(final LabRoom labRoom, final LabRoomDailyBan labRoomDailyBan, final boolean available) {
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
			eventPublisher.publishEvent(new LabRoomDailyUnavailableEvent(this, labRoom, date));
		}
	}

	@Transactional(readOnly = true)
	public LabRoomAvailableResponse getAvailableByDate(final String name, final LocalDate date) {
		LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		boolean availableByDate = labRoomDailyBanRepository.findByLabRoomIdAndBanDate(labRoom.getId(), date)
			.isEmpty() && labRoom.isAvailable();
		return new LabRoomAvailableResponse(labRoom.getId(), availableByDate, date);
	}

	@Transactional(readOnly = true)
	public LabRoomAvailableResponse getAvailable(final String name) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		final boolean available = labRoom.isAvailable();
		return new LabRoomAvailableResponse(labRoom.getId(), available, null);
	}
}
