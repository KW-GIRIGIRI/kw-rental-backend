package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.rentableAsset;
import static com.girigiri.kwrental.penalty.domain.QPenalty.penalty;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

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

    @Override
    public UserPenaltiesResponse findUserPenaltiesResponseByMemberId(final Long memberId) {
        final List<UserPenaltyResponse> userPenaltyResponses = queryFactory
                .from(penalty)
                .join(reservationSpec).on(reservationSpec.id.eq(penalty.reservationSpecId))
                .join(rentableAsset).on(rentableAsset.id.eq(reservationSpec.rentable.id))
                .where(penalty.memberId.eq(memberId))
                .select(Projections.constructor(UserPenaltyResponse.class, penalty.id, penalty.period, rentableAsset.name, penalty.reason))
                .fetch();
        return new UserPenaltiesResponse(userPenaltyResponses);
    }
}
