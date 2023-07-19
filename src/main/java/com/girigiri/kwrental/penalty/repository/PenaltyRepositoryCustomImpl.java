package com.girigiri.kwrental.penalty.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;
import static com.girigiri.kwrental.penalty.domain.QPenalty.*;
import static com.girigiri.kwrental.rental.domain.QAbstractRentalSpec.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservation.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservationSpec.*;
import static com.girigiri.kwrental.util.QueryDSLUtils.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
			.join(abstractRentalSpec).on(abstractRentalSpec.id.eq(penalty.rentalSpecId))
			.where(penalty.memberId.eq(memberId))
			.select(Projections.constructor(UserPenaltyResponse.class, penalty.id, abstractRentalSpec.acceptDateTime,
				abstractRentalSpec.returnDateTime, penalty.period, rentableAsset.name,
				penalty.reason))
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
