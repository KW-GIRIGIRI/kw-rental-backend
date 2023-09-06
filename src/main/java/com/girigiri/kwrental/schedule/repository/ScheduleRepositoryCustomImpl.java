package com.girigiri.kwrental.schedule.repository;

import static com.girigiri.kwrental.schedule.domain.QSchedule.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public long deleteAllSchedules() {
		return queryFactory.delete(schedule)
			.execute();
	}
}
