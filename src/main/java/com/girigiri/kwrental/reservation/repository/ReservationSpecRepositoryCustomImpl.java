package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.dto.QReservedAmount;
import com.girigiri.kwrental.reservation.repository.dto.ReservedAmount;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

public class ReservationSpecRepositoryCustomImpl implements ReservationSpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public ReservationSpecRepositoryCustomImpl(final JPAQueryFactory queryFactory, final EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<ReservationSpec> findOverlappedByPeriod(final Long equipmentId, final RentalPeriod rentalPeriod) {
        final LocalDate start = rentalPeriod.getRentalStartDate();
        final LocalDate end = rentalPeriod.getRentalEndDate();
        final List<ReservationSpec> overlappedLeft = queryFactory.selectFrom(reservationSpec)
                .where(reservationSpec.equipment.id.eq(equipmentId)
                        .and(reservationSpec.period.rentalStartDate.loe(start))
                        .and(reservationSpec.period.rentalEndDate.after(start)))
                .fetch();
        final List<ReservationSpec> overLappedRight = queryFactory.selectFrom(reservationSpec)
                .where(reservationSpec.equipment.id.eq(equipmentId)
                        .and(reservationSpec.period.rentalStartDate.after(start))
                        .and(reservationSpec.period.rentalStartDate.before(end)))
                .fetch();
        return Stream.concat(overlappedLeft.stream(), overLappedRight.stream())
                .distinct().toList();
    }

    @Override
    public List<ReservationSpec> findOverlappedBetween(final Long equipmentId, final LocalDate start, final LocalDate end) {
        return findOverlappedByPeriod(equipmentId, new RentalPeriod(start, end.plusDays(1)));
    }

    @Override
    public List<ReservedAmount> findRentalAmountsByEquipmentIds(final List<Long> equipmentIds, final LocalDate date) {
        return queryFactory
                .select(
                        new QReservedAmount(equipment.id, equipment.totalQuantity, reservationSpec.amount.amount.sum().coalesce(0))
                )
                .from(reservationSpec)
                .rightJoin(equipment).on(reservationSpec.equipment.id.eq(equipment.id).
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
                        reservationSpec.equipment.id.eq(equipmentId)
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
}
