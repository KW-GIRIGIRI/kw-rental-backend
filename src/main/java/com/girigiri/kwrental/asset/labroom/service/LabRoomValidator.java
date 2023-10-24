package com.girigiri.kwrental.asset.labroom.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class LabRoomValidator {

	private final LabRoomDailyBanRepository labRoomDailyBanRepository;

	public void validateDays(final LabRoom labRoom, final Set<LocalDate> rentalDays) {
		List<LabRoomDailyBan> bans = labRoomDailyBanRepository.findByLabRoomId(labRoom.getId());
		boolean isBanned = bans.stream()
			.anyMatch(ban -> ban.hasAny(rentalDays));
		if (isBanned)
			throw new LabRoomNotAvailableException();
	}

	public void validateAvailable(final LabRoom labRoom) {
		if (!labRoom.isAvailable())
			throw new LabRoomNotAvailableException();
	}
}
