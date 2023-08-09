package com.girigiri.kwrental.asset.labroom.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class LabRoomDailyBanRetriever {

	private final LabRoomDailyBanRepository labRoomDailyBanRepository;

	public Map<LocalDate, LabRoomDailyBan> getLabRoomBanByDates(final Long id, final LocalDate from,
		final LocalDate to) {
		return labRoomDailyBanRepository.findByLabRoomIdAndInclusive(id, from, to)
			.stream()
			.collect(Collectors.toMap(LabRoomDailyBan::getBanDate, Function.identity()));
	}
}
