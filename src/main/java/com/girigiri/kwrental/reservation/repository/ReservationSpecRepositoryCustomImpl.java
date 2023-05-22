package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.repository.dto.QReservedAmount;
import com.girigiri.kwrental.reservation.repository.dto.ReservedAmount;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.girigiri.kwrental.auth.domain.QMember.member;
import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

public class ReservationSpecRepositoryCustomImpl implements ReservationSpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public ReservationSpecRepositoryCustomImpl(final JPAQueryFactory queryFactory, final EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<ReservationSpec> findOverlappedBetween(final Long rentableId, final LocalDate start, final LocalDate end) {
        return findOverlappedByPeriod(rentableId, new RentalPeriod(start, end.plusDays(1)));
    }

    @Override
    public List<ReservationSpec> findOverlappedByPeriod(final Long rentableId, final RentalPeriod rentalPeriod) {
        final LocalDate start = rentalPeriod.getRentalStartDate();
        final LocalDate end = rentalPeriod.getRentalEndDate();
        final List<ReservationSpec> overlappedLeft = queryFactory.selectFrom(reservationSpec)
                .where(reservationSpec.rentable.id.eq(rentableId)
                        .and(reservationSpec.period.rentalStartDate.loe(start))
                        .and(reservationSpec.period.rentalEndDate.after(start)))
                .fetch();
        final List<ReservationSpec> overLappedRight = queryFactory.selectFrom(reservationSpec)
                .where(reservationSpec.rentable.id.eq(rentableId)
                        .and(reservationSpec.period.rentalStartDate.after(start))
                        .and(reservationSpec.period.rentalStartDate.before(end)))
                .fetch();
        return Stream.concat(overlappedLeft.stream(), overLappedRight.stream())
                .distinct().toList();
    }

    @Override
    public List<ReservedAmount> findRentalAmountsByEquipmentIds(final List<Long> equipmentIds, final LocalDate date) {
        return queryFactory
                .select(
                        new QReservedAmount(equipment.id, equipment.totalQuantity, reservationSpec.amount.amount.sum().coalesce(0))
                )
                .from(reservationSpec)
                .rightJoin(equipment).on(reservationSpec.rentable.id.eq(equipment.id).
                        and(reservationSpec.period.rentalStartDate.loe(date))
                        .and(reservationSpec.period.rentalEndDate.after(date)))
                .where(equipment.id.in(equipmentIds))
                .groupBy(equipment.id)
                .fetch();
    }

    @Override
    public List<ReservationSpec> findByStartDateBetween(final Long equipmentId, final LocalDate start, final LocalDate end) {
        return queryFactory
                .selectFrom(reservationSpec)
                .leftJoin(reservationSpec.reservation).fetchJoin()
                .where(
                        reservationSpec.rentable.id.eq(equipmentId)
                                .and(reservationSpec.period.rentalStartDate.goe(start))
                                .and(reservationSpec.period.rentalStartDate.loe(end)))
                .fetch();
    }

    @Override
    public void adjustAmountAndStatus(final ReservationSpec reservationSpecForUpdate) {
        entityManager.detach(reservationSpecForUpdate);
        queryFactory.update(reservationSpec)
                .set(reservationSpec.amount, reservationSpecForUpdate.getAmount())
                .set(reservationSpec.status, reservationSpecForUpdate.getStatus())
                .where(reservationSpec.id.eq(reservationSpecForUpdate.getId()))
                .execute();
        entityManager.merge(reservationSpecForUpdate);
    }

    @Override
    public Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenAccept(final LocalDate date) {
        return findEquipmentReservationWhere(
                reservationSpec.rentable.instanceOf(Equipment.class),
                reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RENTED),
                reservationSpec.period.rentalStartDate.eq(date)
        );
    }

    private Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhere(final Predicate... predicates) {
        return Set.copyOf(queryFactory
                .from(reservationSpec)
                .leftJoin(reservation).on(reservationSpec.reservation.id.eq(reservation.id))
                .leftJoin(reservationSpec.rentable).fetchJoin()
                .leftJoin(member).on(member.id.eq(reservation.memberId))
                .where(predicates)
                .transform(groupBy(reservation.id)
                        .list(Projections.constructor(EquipmentReservationWithMemberNumber.class,
                                reservation.id, reservation.name, member.memberNumber, reservation.acceptDateTime, list(reservationSpec)))
                )
        );
    }

    @Override
    public Set<EquipmentReservationWithMemberNumber> findOverdueEquipmentReservationWhenReturn(final LocalDate date) {
        return findEquipmentReservationWhere(
                reservationSpec.rentable.instanceOf(Equipment.class),
                reservationSpec.status.eq(ReservationSpecStatus.OVERDUE_RENTED),
                reservationSpec.period.rentalEndDate.before(date)
        );
    }

    @Override
    public Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenReturn(final LocalDate date) {
        return findEquipmentReservationWhere(
                reservationSpec.rentable.instanceOf(Equipment.class),
                reservationSpec.status.eq(ReservationSpecStatus.RENTED),
                reservationSpec.period.rentalEndDate.eq(date)
        );
    }
}
