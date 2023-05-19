package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static com.girigiri.kwrental.auth.domain.QMember.member;
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
    public Set<ReservationWithMemberNumber> findUnterminatedReservationsWithSpecsByStartDate(final LocalDate startDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservation.terminated.isFalse(), reservationSpec.period.rentalStartDate.eq(startDate))
                .fetch());
    }

    @Override
    public Set<ReservationWithMemberNumber> findUnterminatedOverdueReservationWithSpecs(final LocalDate returnDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservation.terminated.isFalse(), reservationSpec.period.rentalEndDate.before(returnDate))
                .fetch());
    }

    @Override
    public Optional<Reservation> findByIdWithSpecs(final Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.reservationSpecs).fetchJoin()
                .where(reservation.id.eq(id))
                .fetchOne());
    }

    private JPAQuery<ReservationWithMemberNumber> selectReservationWithMemberNumberAndEquipmentAndSpecs() {
        return jpaQueryFactory
                .select(Projections.constructor(ReservationWithMemberNumber.class, reservation, member.memberNumber))
                .from(reservation)
                .leftJoin(reservation.reservationSpecs, reservationSpec).fetchJoin()
                .leftJoin(reservationSpec.equipment).fetchJoin()
                .leftJoin(member).on(member.id.eq(reservation.memberId));
    }

    @Override
    public Set<ReservationWithMemberNumber> findUnterminatedReservationsWithSpecsByEndDate(final LocalDate endDate) {
        return Set.copyOf(selectReservationWithMemberNumberAndEquipmentAndSpecs()
                .where(reservation.terminated.isFalse(), reservationSpec.period.rentalEndDate.eq(endDate))
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
