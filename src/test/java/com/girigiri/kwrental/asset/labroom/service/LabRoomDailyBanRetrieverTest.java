package com.girigiri.kwrental.asset.labroom.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;

@ExtendWith(MockitoExtension.class)
class LabRoomDailyBanRetrieverTest {

	@Mock
	private LabRoomDailyBanRepository labRoomDailyBanRepository;
	@InjectMocks
	private LabRoomDailyBanRetriever labRoomDailyBanRetriever;

	@Test
	@DisplayName("특정 랩실의 일자별 금지 객체를 조회한다.")
	void getLabRoomBanByDates() {
		// given
		final Long labRoomId = 1L;
		final LabRoomDailyBan dailyBan = LabRoomDailyBan.builder()
			.banDate(LocalDate.now())
			.labRoomId(labRoomId)
			.build();
		given(labRoomDailyBanRepository.findByLabRoomIdAndInclusive(labRoomId, LocalDate.now(),
			LocalDate.now().plusDays(1))).willReturn(List.of(dailyBan));

		// when
		final Map<LocalDate, LabRoomDailyBan> actual = labRoomDailyBanRetriever.getLabRoomBanByDates(labRoomId,
			LocalDate.now(), LocalDate.now().plusDays(1));

		// then
		Assertions.assertThat(actual).containsExactly(Map.entry(LocalDate.now(), dailyBan));
	}
}