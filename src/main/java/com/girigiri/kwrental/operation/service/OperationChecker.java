package com.girigiri.kwrental.operation.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

	public boolean canOperate(final LocalDate... dates) {
		return canOperate(Arrays.stream(dates));
	}

	public boolean canOperate(final Collection<LocalDate> dates) {
		return canOperate(dates.stream());
	}

	private boolean canOperate(final Stream<LocalDate> dateStream) {
		if (!isEntireOperating())
			return false;
		final List<Schedule> schedules = scheduleRepository.findAll();
		return dateStream.allMatch(it -> canOperate(schedules, it));
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

	public Set<LocalDate> getOperateDates(final LocalDate startInclusive, final LocalDate endInclusive) {
		return getOperateDates(
			Stream.iterate(startInclusive, it -> it.isBefore(endInclusive) || it.equals(endInclusive),
				it -> it.plusDays(1)));
	}

	public Set<LocalDate> getOperateDates(final Collection<LocalDate> dates) {
		return getOperateDates(dates.stream());
	}

	public Set<LocalDate> getOperateDates(final Stream<LocalDate> dateStream) {
		if (!isEntireOperating())
			return Collections.emptySet();
		final List<Schedule> schedules = scheduleRepository.findAll();
		return dateStream.filter(it -> canOperate(schedules, it)).collect(Collectors.toSet());
	}
}
