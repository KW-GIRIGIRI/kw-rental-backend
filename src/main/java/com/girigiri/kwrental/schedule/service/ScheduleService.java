package com.girigiri.kwrental.schedule.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.schedule.domain.Schedule;
import com.girigiri.kwrental.schedule.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.schedule.dto.request.PutSchedulesRequest.PutScheduleRequest;
import com.girigiri.kwrental.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final EntireOperationService entireOperationService;

	@Transactional
	public void putSchedules(final PutSchedulesRequest putSchedulesRequest) {
		scheduleRepository.deleteAllSchedules();
		final List<Schedule> schedules = mapToSchedules(putSchedulesRequest.schedules());
		scheduleRepository.saveAll(schedules);
		entireOperationService.putEntireOperation(putSchedulesRequest.isRunning());
	}

	private List<Schedule> mapToSchedules(final List<PutScheduleRequest> putScheduleRequests) {
		return putScheduleRequests.stream()
			.map(this::mapToSchedule)
			.toList();
	}

	private Schedule mapToSchedule(final PutScheduleRequest it) {
		return Schedule.builder()
			.dayOfWeek(it.dayOfWeek())
			.startAt(it.startAt())
			.endAt(it.endAt())
			.build();
	}
}
