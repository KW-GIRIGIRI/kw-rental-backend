package com.girigiri.kwrental.schedule.repository;

import static com.girigiri.kwrental.schedule.domain.QEntireOperation.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntireOperationRepositoryCustomImpl implements EntireOperationRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public long updateEntireOperation(final boolean isRunning) {
		return queryFactory.update(entireOperation)
			.set(entireOperation.isRunning, isRunning).execute();
	}

	@Override
	public boolean exists() {
		return queryFactory.selectFrom(entireOperation)
			.fetchFirst() != null;
	}
}
