package com.girigiri.kwrental.schedule.repository;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.schedule.domain.Schedule;

public interface ScheduleRepository extends Repository<Schedule, Long>, ScheduleRepositoryCustom {

	Iterable<Schedule> saveAll(Iterable<Schedule> schedules);
}
