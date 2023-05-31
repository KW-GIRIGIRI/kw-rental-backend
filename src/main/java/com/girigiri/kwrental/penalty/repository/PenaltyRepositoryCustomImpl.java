package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.rentableAsset;
import static com.girigiri.kwrental.penalty.domain.QPenalty.penalty;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;
import static com.girigiri.kwrental.util.QueryDSLUtils.setPageable;

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

    @Override
    public Page<PenaltyHistoryResponse> findPenaltyHistoryPageResponse(final Pageable pageable) {
        final JPAQuery<PenaltyHistoryResponse> query = queryFactory
                .from(penalty)
                .join(reservationSpec).on(reservationSpec.id.eq(penalty.reservationSpecId))
                .join(rentableAsset).on(rentableAsset.id.eq(reservationSpec.rentable.id))
                .join(reservation).on(reservation.id.eq(penalty.reservationId))
                .select(Projections.constructor(PenaltyHistoryResponse.class,
                        penalty.id, reservation.name, penalty.period, rentableAsset.name, penalty.reason));
        setPageable(query, penalty, pageable);
        return new PageImpl<>(query.fetch(), pageable, countAll());
    }

    private long countAll() {
        final Long count = queryFactory
                .select(penalty.count())
                .from(penalty)
                .fetchOne();
        return count == null ? 0 : count;
    }
}
