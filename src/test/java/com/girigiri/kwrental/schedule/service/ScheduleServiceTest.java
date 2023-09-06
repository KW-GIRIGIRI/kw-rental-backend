package com.girigiri.kwrental.schedule.service;

import static com.girigiri.kwrental.schedule.dto.request.PutSchedulesRequest.*;
import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.schedule.domain.Schedule;
import com.girigiri.kwrental.schedule.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.schedule.repository.ScheduleRepository;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

	@Mock
	private ScheduleRepository scheduleRepository;
	@Mock
	private EntireOperationService operationService;
	@InjectMocks
	private ScheduleService scheduleService;

	@Test
	@DisplayName("랩실 일정을 설정한다.")
	void putSchedules() {
		// given
		final PutScheduleRequest putScheduleRequest = PutScheduleRequest.builder()
			.dayOfWeek(DayOfWeek.MONDAY)
			.startAt(LocalTime.MIN)
			.endAt(LocalTime.MAX)
			.build();
		final PutSchedulesRequest putSchedulesRequest = builder().isRunning(true)
			.schedules(List.of(putScheduleRequest))
			.build();
		final Schedule schedule = ScheduleFixture.builder(DayOfWeek.MONDAY)
			.startAt(LocalTime.MIN)
			.endAt(LocalTime.MAX)
			.build();

		when(scheduleRepository.deleteAllSchedules()).thenReturn(1L);
		when(scheduleRepository.saveAll(deepRefEq(List.of(schedule)))).thenReturn(List.of(schedule));
		willDoNothing().given(operationService).putEntireOperation(true);

		// when, then
		assertThatCode(() -> scheduleService.putSchedules(putSchedulesRequest))
			.doesNotThrowAnyException();
	}
}