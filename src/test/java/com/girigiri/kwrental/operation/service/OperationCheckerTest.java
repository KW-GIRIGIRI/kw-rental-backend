package com.girigiri.kwrental.operation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.operation.repository.EntireOperationRepository;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;
import com.girigiri.kwrental.testsupport.fixture.EntireOperationFixture;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

@ExtendWith(MockitoExtension.class)
class OperationCheckerTest {

	@Mock
	private EntireOperationRepository entireOperationRepository;
	@Mock
	private ScheduleRepository scheduleRepository;
	@InjectMocks
	private OperationChecker operationChecker;

	@Test
	@DisplayName("랩실 무기한 운영이 불가능하면 운영 불가 처리한다.")
	void canOperate_entireUnavailable() {
		// given
		given(entireOperationRepository.findAll()).willReturn(List.of(EntireOperationFixture.create(false)));

		// when
		final boolean actual = operationChecker.canOperate(LocalDate.now(), LocalDate.now().plusDays(1));

		// then
		assertThat(actual).isFalse();
	}

	@Test
	@DisplayName("랩실 운영 요일에 해당하지 않으면 운영 불가 처리한다.")
	void canOperate_scheduleNotMatch() {
		// given
		given(entireOperationRepository.findAll()).willReturn(List.of(EntireOperationFixture.create(true)));
		given(scheduleRepository.findAll()).willReturn(List.of(ScheduleFixture.create(DayOfWeek.MONDAY)));

		// when
		final LocalDate friday = LocalDate.of(2023, 9, 15);
		final boolean actual = operationChecker.canOperate(friday, friday.plusDays(1));

		// then
		assertThat(actual).isFalse();
	}

	@Test
	@DisplayName("랩실 운영이 가능하면 운영 가능 처리한다.")
	void canOperate() {
		// given
		given(entireOperationRepository.findAll()).willReturn(List.of(EntireOperationFixture.create(true)));
		given(scheduleRepository.findAll()).willReturn(
			List.of(ScheduleFixture.create(DayOfWeek.TUESDAY), ScheduleFixture.create(DayOfWeek.FRIDAY)));

		// when
		final LocalDate tuesday = LocalDate.of(2023, 9, 12);
		final LocalDate friday = LocalDate.of(2023, 9, 15);
		final boolean actual = operationChecker.canOperate(friday, tuesday);

		// then
		assertThat(actual).isTrue();
	}

	@Test
	@DisplayName("시작일과 끝잀 사이의 운영되는 날짜를 조회한다.")
	void getOperateDates() {
		// given
		given(entireOperationRepository.findAll()).willReturn(List.of(EntireOperationFixture.create(true)));
		given(scheduleRepository.findAll()).willReturn(List.of(ScheduleFixture.create(DayOfWeek.MONDAY)));
		final LocalDate start = LocalDate.of(2023, 9, 15);
		final LocalDate end = LocalDate.of(2023, 9, 22);

		// when
		final Set<LocalDate> actual = operationChecker.getOperateDates(start, end);

		// then
		assertThat(actual).containsExactlyInAnyOrder(LocalDate.of(2023, 9, 18));
	}
}