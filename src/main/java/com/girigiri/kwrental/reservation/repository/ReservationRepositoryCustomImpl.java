package com.girigiri.kwrental.reservation.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;
import static com.girigiri.kwrental.reservation.domain.QReservation.*;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	public ReservationRepositoryCustomImpl(final JPAQueryFactory queryFactory, final EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
	}

	@Override
	public Optional<Reservation> findByIdWithSpecs(final Long id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.reservationSpecs).fetchJoin()
			.where(reservation.id.eq(id))
			.fetchOne());
	}

	@Override
	public Set<Reservation> findNotTerminatedEquipmentReservationsByMemberId(final Long memberId) {
		return Set.copyOf(selectFromReservationFetchJoinedSpecAndAsset()
			.where(rentableAsset.instanceOf(Equipment.class), reservation.memberId.eq(memberId),
				reservation.terminated.isFalse())
			.fetch()
		);
	}

	private JPAQuery<Reservation> selectFromReservationFetchJoinedSpecAndAsset() {
		return queryFactory
			.selectFrom(reservation)
			.join(reservation.reservationSpecs, reservationSpec).fetchJoin()
			.join(reservationSpec.rentable, rentableAsset).fetchJoin();
	}

	@Override
	public Set<Reservation> findNotTerminatedLabRoomReservationsByMemberId(final Long memberId) {
		return Set.copyOf(selectFromReservationFetchJoinedSpecAndAsset()
			.where(rentableAsset.instanceOf(LabRoom.class), reservation.memberId.eq(memberId),
				reservation.terminated.isFalse())
			.fetch()
		);
	}

	@Override
	public Set<Reservation> findNotTerminatedReservationsByMemberId(final Long memberId) {
		return Set.copyOf(selectFromReservationFetchJoinedSpecAndAsset()
			.where(reservation.memberId.eq(memberId), reservation.terminated.isFalse())
			.fetch()
		);
	}

	@Override
	public List<Reservation> findNotTerminatedRelatedReservation(LabRoomReservation from) {
		return queryFactory.selectFrom(reservation)
			.join(reservationSpec).on(reservationSpec.reservation.id.eq(reservation.id),
				reservationSpec.rentable.id.eq(from.getLabRoomId()))
			.where(reservationSpec.period.eq(from.getPeriod()),
				reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RENTED))
			.fetch();
	}

	@Override
	public void adjustTerminated(final Reservation reservationForUpdate) {
		entityManager.detach(reservationForUpdate);
		queryFactory.update(reservation)
			.set(reservation.terminated, reservationForUpdate.isTerminated())
			.where(reservation.id.eq(reservationForUpdate.getId()))
			.execute();
		entityManager.merge(reservationForUpdate);
	}

	@Override
	public List<Reservation> findByReservationSpecIds(List<Long> reservationSpecIds) {
		final List<Long> reservationIds = queryFactory.select(reservationSpec.reservation.id)
			.from(reservationSpec)
			.where(reservationSpec.id.in(reservationSpecIds))
			.fetch();
		return queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.reservationSpecs, reservationSpec).fetchJoin()
			.leftJoin(reservationSpec.rentable, rentableAsset).fetchJoin()
			.where(reservation.id.in(reservationIds))
			.fetch();
	}
}
