package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.repository.dto.QReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static com.girigiri.kwrental.auth.domain.QMember.member;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReservationRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Set<ReservationWithMemberNumber> findReservationsWithSpecsByStartDate(final LocalDate startDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservationSpec.period.rentalStartDate.eq(startDate))
                .fetch());
    }

    private JPAQuery<ReservationWithMemberNumber> selectReservationWithMemberNumberAndEquipmentAndSpecs() {
        return jpaQueryFactory
                .select(new QReservationWithMemberNumber(reservation, member.memberNumber))
                .from(reservation)
                .leftJoin(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .leftJoin(reservationSpec.equipment).fetchJoin()
                .leftJoin(member).on(member.id.eq(reservation.memberId));
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
    public Set<ReservationWithMemberNumber> findOverdueReservationWithSpecs(final LocalDate returnDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservation.terminated.isFalse()
                        .and(reservationSpec.period.rentalEndDate.before(returnDate)))
                .fetch());
    }

    @Override
    public Set<ReservationWithMemberNumber> findReservationsWithSpecsByEndDate(final LocalDate endDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservationSpec.period.rentalEndDate.eq(endDate))
                .fetch());
    }

    @Override
    public Set<Reservation> findNotTerminatedReservationsByMemberId(final Long memberId) {
        return Set.copyOf(jpaQueryFactory
                .selectFrom(reservation)
                .join(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .join(reservationSpec.equipment).fetchJoin()
                .where(reservation.memberId.eq(memberId)
                        .and(reservation.terminated.isFalse()))
                .fetch()
        );
    }
}
