package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.Set;

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
    public Set<Reservation> findNotTerminatedReservationsByMemberId(final Long memberId) {
        return Set.copyOf(jpaQueryFactory
                .selectFrom(reservation)
                .join(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .join(reservationSpec.rentable).fetchJoin()
                .where(reservation.memberId.eq(memberId)
                        .and(reservation.terminated.isFalse()))
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
}
