package com.girigiri.kwrental.operation.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.operation.domain.Schedule;

public interface ScheduleRepository extends Repository<Schedule, Long>, ScheduleRepositoryCustom {

	List<Schedule> findAll();

	Iterable<Schedule> saveAll(Iterable<Schedule> schedules);
}
