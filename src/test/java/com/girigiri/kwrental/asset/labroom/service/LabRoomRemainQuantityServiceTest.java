package com.girigiri.kwrental.asset.labroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.testsupport.fixture.LabRoomDailyBanFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomRemainQuantityServiceTest {

	@Mock
	private LabRoomRetriever labRoomRetriever;
	@Mock
	private RemainingQuantityService remainingQuantityService;
	@Mock
	private LabRoomDailyBanRetriever labRoomDailyBanRetriever;
	@Mock
	private AssetService assetService;
	@InjectMocks
	private LabRoomRemainQuantityService labRoomRemainQuantityService;

	@Test
	@DisplayName("특정일에 닫은 랩실은 해당 날짜에 남은 자릿 수가 0으로 조회된다")
	void getRemainQuantityByLabRoomName_bannedDate() {
		// given
		final LocalDate now = LocalDate.now();
		final LabRoom hwado = LabRoomFixture.builder()
			.name("hwado")
			.reservationCountPerDay(1)
			.isAvailable(false)
			.build();
		final LabRoomDailyBan ban1 = LabRoomDailyBanFixture.builder().labRoomId(hwado.getId()).banDate(now).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(assetService.getReservableCountPerDate(anyMap(), any()))
			.willReturn(new RemainQuantitiesPerDateResponse(List.of(new RemainQuantityPerDateResponse(now, 3),
				new RemainQuantityPerDateResponse(now.plusDays(1), 3))));
		given(labRoomDailyBanRetriever.getLabRoomBanByDates(hwado.getId(), now, now.plusDays(1)))
			.willReturn(Map.of(now, ban1));

		// when
		final RemainQuantitiesPerDateResponse actual = labRoomRemainQuantityService.getRemainQuantityByLabRoomName(
			hwado.getName(), now, now.plusDays(1));

		// then
		assertThat(actual.getRemainQuantities()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainQuantityPerDateResponse(now, 0),
				new RemainQuantityPerDateResponse(now.plusDays(1), 3));
	}

	@Test
	@DisplayName("특정 랩실의 각 일마다 대여 가능한 횟수를 조회한다.")
	void getRemainReservationCountPerDateResponse() {
		// given
		LocalDate now = LocalDate.now();
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(remainingQuantityService.getReservationCountInclusive(hwado.getId(), now, now.plusDays(1)))
			.willReturn(Map.of(now, 1, now.plusDays(1), 0));

		// when
		RemainReservationCountsPerDateResponse actual = labRoomRemainQuantityService.getRemainReservationCountByLabRoomName(
			"hwado", now, now.plusDays(1));

		// then
		assertThat(actual.getId()).isEqualTo(hwado.getId());
		assertThat(actual.getRemainReservationCounts()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainReservationCountPerDateResponse(now, 0),
				new RemainReservationCountPerDateResponse(now.plusDays(1), 1));
	}

	@Test
	@DisplayName("닫은 랩실은 남은 대여 신청 횟수가 0으로 조회된다.")
	void getRemainReservationCountPerDateResponse_notAvailable() {
		// given
		LocalDate now = LocalDate.now();
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).isAvailable(false).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(remainingQuantityService.getReservationCountInclusive(hwado.getId(), now, now.plusDays(1)))
			.willReturn(Map.of(now, 1, now.plusDays(1), 0));

		// when
		RemainReservationCountsPerDateResponse actual = labRoomRemainQuantityService.getRemainReservationCountByLabRoomName(
			"hwado", now, now.plusDays(1));

		// then
		assertThat(actual.getId()).isEqualTo(hwado.getId());
		assertThat(actual.getRemainReservationCounts()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainReservationCountPerDateResponse(now, 0),
				new RemainReservationCountPerDateResponse(now.plusDays(1), 0));
	}

	@Test
	@DisplayName("특정 날짜에 닫은 랩실은 해당 날짜의 남은 대여 신청 횟수가 0으로 조회된다.")
	void getRemainReservationCountPerDateResponse_ban() {
		// given
		LocalDate now = LocalDate.now();
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(remainingQuantityService.getReservationCountInclusive(hwado.getId(), now, now.plusDays(1)))
			.willReturn(Map.of(now, 0, now.plusDays(1), 0));
		final LabRoomDailyBan ban1 = LabRoomDailyBanFixture.builder().labRoomId(hwado.getId()).banDate(now).build();
		given(labRoomDailyBanRetriever.getLabRoomBanByDates(any(), any(), any()))
			.willReturn(Map.of(now, ban1));

		// when
		RemainReservationCountsPerDateResponse actual = labRoomRemainQuantityService.getRemainReservationCountByLabRoomName(
			"hwado", now, now.plusDays(1));

		// then
		assertThat(actual.getId()).isEqualTo(hwado.getId());
		assertThat(actual.getRemainReservationCounts()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainReservationCountPerDateResponse(now, 0),
				new RemainReservationCountPerDateResponse(now.plusDays(1), 1));
	}
}