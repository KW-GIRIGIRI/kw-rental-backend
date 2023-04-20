package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.QReservedAmount;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

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
                        new QReservedAmount(rentalSpec.equipment.id, rentalSpec.equipment.totalQuantity, rentalSpec.amount.amount.sum())
                )
                .from(rentalSpec)
                .where(rentalSpec.period.rentalStartDate.loe(date).and(rentalSpec.period.rentalEndDate.after(date)).and(rentalSpec.equipment.id.in(equipmentIds)))
                .fetch();
    }
}
