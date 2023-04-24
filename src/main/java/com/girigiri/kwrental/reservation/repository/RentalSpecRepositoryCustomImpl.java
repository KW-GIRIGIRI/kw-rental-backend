package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.dto.QReservedAmount;
import com.girigiri.kwrental.reservation.dto.ReservedAmount;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.reservation.domain.QRentalSpec.rentalSpec;

public class RentalSpecRepositoryCustomImpl implements RentalSpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RentalSpecRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RentalSpec> findOverlappedByPeriod(final Long equipmentId, final RentalPeriod rentalPeriod) {
        final LocalDate start = rentalPeriod.getRentalStartDate();
        final LocalDate end = rentalPeriod.getRentalEndDate();
        final List<RentalSpec> overlappedLeft = queryFactory.selectFrom(rentalSpec)
                .where(rentalSpec.equipment.id.eq(equipmentId)
                        .and(rentalSpec.period.rentalStartDate.loe(start))
                        .and(rentalSpec.period.rentalEndDate.after(start)))
                .fetch();
        final List<RentalSpec> overLappedRight = queryFactory.selectFrom(rentalSpec)
                .where(rentalSpec.equipment.id.eq(equipmentId)
                        .and(rentalSpec.period.rentalStartDate.after(start))
                        .and(rentalSpec.period.rentalStartDate.before(end)))
                .fetch();
        return Stream.concat(overlappedLeft.stream(), overLappedRight.stream())
                .distinct().toList();
    }

    @Override
    public List<ReservedAmount> findRentalAmountsByEquipmentIds(final List<Long> equipmentIds, final LocalDate date) {
        return queryFactory
                .select(
                        new QReservedAmount(equipment.id, equipment.totalQuantity, rentalSpec.amount.amount.sum().coalesce(0))
                )
                .from(rentalSpec)
                .rightJoin(equipment).on(rentalSpec.equipment.id.eq(equipment.id).
                        and(rentalSpec.period.rentalStartDate.loe(date))
                        .and(rentalSpec.period.rentalEndDate.after(date)))
                .where(equipment.id.in(equipmentIds))
                .groupBy(equipment.id)
                .fetch();
    }

    @Override
    public List<RentalSpec> findByStartDateBetween(final Long equipmentId, final LocalDate start, final LocalDate end) {
        return queryFactory
                .selectFrom(rentalSpec)
                .leftJoin(rentalSpec.reservation).fetchJoin()
                .where(
                        rentalSpec.period.rentalStartDate.goe(start)
                                .and(rentalSpec.period.rentalStartDate.loe(end)))
                .fetch();
    }
}
