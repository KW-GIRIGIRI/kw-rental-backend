package com.girigiri.kwrental.asset.labroom.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomAvailableDateFailureException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.asset.labroom.service.event.LabRoomDailyUnavailableEvent;
import com.girigiri.kwrental.asset.labroom.service.event.LabRoomUnavailableEvent;
import com.girigiri.kwrental.testsupport.fixture.LabRoomDailyBanFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomAvailableServiceTest {

	@Mock
	private LabRoomRetriever labRoomRetriever;
	@Mock
	private LabRoomRepository labRoomRepository;
	@Mock
	private LabRoomDailyBanRepository labRoomDailyBanRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@InjectMocks
	private LabRoomAvailableService labRoomAvailableService;

	@Test
	@DisplayName("랩실 전체기간 운영 여부를 설정한다.")
	void setAvailableForEntirePeriod() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		doNothing().when(labRoomRepository).updateAvailable(hwado.getId(), false);
		doNothing().when(eventPublisher).publishEvent(any(LabRoomUnavailableEvent.class));

		// when
		labRoomAvailableService.setAvailableForEntirePeriod("hwado", false);

		// then
		verify(labRoomRepository).updateAvailable(hwado.getId(), false);
	}

	@Test
	@DisplayName("랩실 특정 일자에 운영하지 않고 있을 때 운영되도록 한다.")
	void setAvailable_setTrueWhenFalse() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		final LabRoomDailyBan dailyBan = LabRoomDailyBanFixture.builder()
			.id(1L)
			.labRoomId(hwado.getId())
			.banDate(LocalDate.now())
			.build();
		given(labRoomDailyBanRepository.findByLabRoomIdAndBanDate(hwado.getId(), LocalDate.now())).willReturn(
			Optional.of(dailyBan));
		doNothing().when(labRoomDailyBanRepository).deleteById(dailyBan.getId());

		// when
		labRoomAvailableService.setAvailable("hwado", LocalDate.now(), true);

		// then
		verify(labRoomDailyBanRepository).deleteById(dailyBan.getId());
	}

	@Test
	@DisplayName("랩실 특정 일자에 운영하지 않고 있을 때 운영되도록 할 때 전체 기간 운영되지 않은 상태면 예외가 발생한다.")
	void setAvailable_setTrueWhenFalse_available() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).isAvailable(false).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		final LocalDate now = LocalDate.now();
		final LabRoomDailyBan dailyBan = LabRoomDailyBanFixture.builder()
			.id(1L)
			.labRoomId(hwado.getId())
			.banDate(now)
			.build();
		given(labRoomDailyBanRepository.findByLabRoomIdAndBanDate(hwado.getId(), now)).willReturn(
			Optional.of(dailyBan));

		// when, then
		assertThatThrownBy(() -> labRoomAvailableService.setAvailable("hwado", now, true))
			.isExactlyInstanceOf(LabRoomAvailableDateFailureException.class);
	}

	@Test
	@DisplayName("랩실 특정 일자에 운영되고 있을 때 운영되지 않도록 한다.")
	void setAvailable_setFalseWhenTrue() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(labRoomDailyBanRepository.findByLabRoomIdAndBanDate(hwado.getId(), LocalDate.now())).willReturn(
			Optional.empty());
		final LabRoomDailyBan dailyBan = LabRoomDailyBanFixture.builder()
			.labRoomId(hwado.getId())
			.banDate(LocalDate.now())
			.build();
		given(labRoomDailyBanRepository.save(deepRefEq(dailyBan))).willReturn(dailyBan);
		doNothing().when(eventPublisher).publishEvent(any(LabRoomDailyUnavailableEvent.class));

		// when
		labRoomAvailableService.setAvailable("hwado", LocalDate.now(), false);

		// then
		verify(labRoomDailyBanRepository).save(deepRefEq(dailyBan));
	}

	@Test
	@DisplayName("해당날짜에 운영 가능 여부를 조회한다.")
	void getAvailableByDate() {
		// given
		LabRoom hwado = LabRoomFixture.builder().id(1L).name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		given(labRoomDailyBanRepository.findByLabRoomIdAndBanDate(hwado.getId(), LocalDate.now()))
			.willReturn(Optional.empty());

		// when
		final LabRoomAvailableResponse actual = labRoomAvailableService.getAvailableByDate("hwado", LocalDate.now());

		// then
		assertThat(actual).isEqualTo(new LabRoomAvailableResponse(hwado.getId(), true, LocalDate.now()));
	}

	@Test
	@DisplayName("랩실 전체 기간 운영 여부를 조회한다.")
	void getAvailable() {
		// given
		LabRoom hwado = LabRoomFixture.builder().id(1L).name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);

		// when
		final LabRoomAvailableResponse actual = labRoomAvailableService.getAvailable("hwado");

		// then
		assertThat(actual).isEqualTo(new LabRoomAvailableResponse(hwado.getId(), true, null));
	}
}