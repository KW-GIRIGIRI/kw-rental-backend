package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import static com.girigiri.kwrental.penalty.domain.QPenalty.penalty;

public class PenaltyRepositoryCustomImpl implements PenaltyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PenaltyRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Penalty> findByOngoingPenalties(final Long memberId) {
        final LocalDate now = LocalDate.now();
        return queryFactory
                .selectFrom(penalty)
                .where(penalty.memberId.eq(memberId),
                        penalty.period.startDate.loe(now),
                        penalty.period.endDate.goe(now))
                .fetch();
    }
}
