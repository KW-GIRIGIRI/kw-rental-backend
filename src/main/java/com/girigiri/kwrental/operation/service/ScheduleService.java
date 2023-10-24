package com.girigiri.kwrental.operation.service;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.operation.domain.Schedule;
import com.girigiri.kwrental.operation.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.operation.dto.response.SchedulesResponse;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;

	@Transactional
	public void putSchedules(final PutSchedulesRequest putSchedulesRequest) {
		scheduleRepository.deleteAllSchedules();
		final List<Schedule> schedules = mapToSchedules(putSchedulesRequest.schedules());
		scheduleRepository.saveAll(schedules);
	}

	private List<Schedule> mapToSchedules(final List<DayOfWeek> putScheduleRequests) {
		return putScheduleRequests.stream()
			.map(this::mapToSchedule)
			.toList();
	}

	private Schedule mapToSchedule(final DayOfWeek it) {
		return Schedule.builder()
			.dayOfWeek(it)
			.build();
	}

	@Transactional(readOnly = true)
	public SchedulesResponse getSchedules() {
		final List<DayOfWeek> dayOfWeeks = scheduleRepository.findAll()
			.stream()
			.map(Schedule::getDayOfWeek)
			.toList();
		return new SchedulesResponse(dayOfWeeks);
	}
}
