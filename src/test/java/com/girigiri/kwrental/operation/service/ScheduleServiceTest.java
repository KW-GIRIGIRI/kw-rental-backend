package com.girigiri.kwrental.operation.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.operation.domain.Schedule;
import com.girigiri.kwrental.operation.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

	@Mock
	private ScheduleRepository scheduleRepository;
	@InjectMocks
	private ScheduleService scheduleService;

	@Test
	@DisplayName("랩실 일정을 설정한다.")
	void putSchedules() {
		// given
		final PutSchedulesRequest putSchedulesRequest = PutSchedulesRequest.builder()
			.schedules(List.of(DayOfWeek.MONDAY))
			.build();
		final Schedule schedule = ScheduleFixture.builder(DayOfWeek.MONDAY)
			.build();

		when(scheduleRepository.deleteAllSchedules()).thenReturn(1L);
		when(scheduleRepository.saveAll(deepRefEq(List.of(schedule)))).thenReturn(List.of(schedule));

		// when, then
		assertThatCode(() -> scheduleService.putSchedules(putSchedulesRequest))
			.doesNotThrowAnyException();
	}
}