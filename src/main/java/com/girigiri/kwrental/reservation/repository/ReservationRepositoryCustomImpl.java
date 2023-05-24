package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.rentableAsset;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public ReservationRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory, final EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Reservation> findByIdWithSpecs(final Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.reservationSpecs).fetchJoin()
                .where(reservation.id.eq(id))
                .fetchOne());
    }

    @Override
    public Set<Reservation> findNotTerminatedEquipmentReservationsByMemberId(final Long memberId) {
        return Set.copyOf(jpaQueryFactory
                .selectFrom(reservation)
                .join(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .join(reservationSpec.rentable, rentableAsset).fetchJoin()
                .where(rentableAsset.instanceOf(Equipment.class), reservation.memberId.eq(memberId)
                        , reservation.terminated.isFalse())
                .fetch()
        );
    }

    @Override
    public void adjustTerminated(final Reservation reservationForUpdate) {
        entityManager.detach(reservationForUpdate);
        jpaQueryFactory.update(reservation)
                .set(reservation.terminated, reservationForUpdate.isTerminated())
                .where(reservation.id.eq(reservationForUpdate.getId()))
                .execute();
        entityManager.merge(reservationForUpdate);
    }

    @Override
    public List<Reservation> findByReservationSpecIds(List<Long> reservationSpecIds) {
        final List<Long> reservationIds = jpaQueryFactory.select(reservationSpec.reservation.id)
                .from(reservationSpec)
                .where(reservationSpec.id.in(reservationSpecIds))
                .fetch();
        return jpaQueryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .leftJoin(reservationSpec.rentable, rentableAsset).fetchJoin()
                .where(reservation.id.in(reservationIds))
                .fetch();
    }
}
