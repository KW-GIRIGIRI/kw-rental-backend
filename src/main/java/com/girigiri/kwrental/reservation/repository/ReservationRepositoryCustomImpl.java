package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReservationRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Reservation> findReservationsWithSpecsByStartDate(final LocalDate startDate) {
        return jpaQueryFactory.selectFrom(reservation)
                .join(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .join(reservationSpec.equipment).fetchJoin()
                .where(reservationSpec.period.rentalStartDate.eq(startDate))
                .fetch();
    }

    @Override
    public Optional<Reservation> findByIdWithSpecs(final Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.reservationSpecs).fetchJoin()
                .where(reservation.id.eq(id))
                .fetchOne());
    }
}
