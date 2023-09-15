package com.girigiri.kwrental.operation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.operation.domain.EntireOperation;
import com.girigiri.kwrental.operation.domain.Schedule;
import com.girigiri.kwrental.operation.repository.EntireOperationRepository;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class OperationChecker {
	private final EntireOperationRepository entireOperationRepository;
	private final ScheduleRepository scheduleRepository;

	public boolean canOperate(final LocalDate start, final LocalDate end) {
		if (!isEntireOperating())
			return false;
		final List<Schedule> schedules = scheduleRepository.findAll();
		return Stream.iterate(start, it -> it.isBefore(end), it -> it.plusDays(1))
			.allMatch(it -> canOperate(schedules, it));
	}

	private boolean canOperate(final List<Schedule> schedules, final LocalDate date) {
		return schedules.stream().anyMatch(schedule -> schedule.canOperatesAt(date));
	}

	private boolean isEntireOperating() {
		return entireOperationRepository.findAll()
			.stream()
			.map(EntireOperation::isRunning)
			.findFirst().orElse(false);
	}

}
