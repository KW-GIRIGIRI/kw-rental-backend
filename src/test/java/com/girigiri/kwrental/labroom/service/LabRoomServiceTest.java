package com.girigiri.kwrental.labroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomServiceTest {

	@Mock
	private LabRoomRepository labRoomRepository;

	@Mock
	private RemainingQuantityService remainingQuantityService;

	@InjectMocks
	private LabRoomService labRoomService;

	@Test
	@DisplayName("특정 랩실의 각 일마다 대여 가능한 횟수를 조회한다.")
	void getRemainReservationCountPerDateResponse() {
		// given
		LocalDate now = LocalDate.now();
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRepository.findLabRoomByName("hwado"))
			.willReturn(Optional.of(hwado));
		given(remainingQuantityService.getReservationCountInclusive(hwado.getId(), now, now.plusDays(1)))
			.willReturn(Map.of(now, 1, now.plusDays(1), 0));

		// when
		RemainReservationCountsPerDateResponse actual = labRoomService.getRemainReservationCountByLabRoomName(
			"hwado", now, now.plusDays(1));

		// then
		assertThat(actual.getId()).isEqualTo(hwado.getId());
		assertThat(actual.getRemainReservationCounts()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainReservationCountPerDateResponse(now, 0),
				new RemainReservationCountPerDateResponse(now.plusDays(1), 1));
	}
}