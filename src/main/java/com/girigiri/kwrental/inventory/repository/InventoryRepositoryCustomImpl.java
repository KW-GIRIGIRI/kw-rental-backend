package com.girigiri.kwrental.inventory.repository;

import static com.girigiri.kwrental.inventory.domain.QInventory.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Inventory> findAllWithEquipment(final Long memberId) {
        return jpaQueryFactory.selectFrom(inventory)
            .join(inventory.asset).fetchJoin()
            .where(inventory.memberId.eq(memberId))
            .fetch();
    }

    @Override
    public int deleteAll(final Long memberId) {
        return (int)jpaQueryFactory.delete(inventory)
            .where(inventory.memberId.eq(memberId))
            .execute();
    }

    @Override
    public Optional<Inventory> findWithEquipmentById(final Long id) {
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(inventory)
                .leftJoin(inventory.asset).fetchJoin()
                .where(inventory.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public Optional<Inventory> findByPeriodAndEquipmentIdAndMemberId(final RentalPeriod rentalPeriod, final Long equipmentId, final Long memberId) {
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(inventory)
                .where(inventory.memberId.eq(memberId), inventory.asset.id.eq(equipmentId),
                    inventory.rentalPeriod.eq(rentalPeriod))
                .fetchOne()
        );
    }

    @Override
    public void updateAmount(final Long id, final RentalAmount amount) {
        jpaQueryFactory.update(inventory)
            .set(inventory.rentalAmount, amount)
            .where(inventory.id.eq(id))
            .execute();
    }

    @Override
    public void deleteByEquipmentId(Long assetId) {
        jpaQueryFactory.delete(inventory)
            .where(inventory.asset.id.eq(assetId))
            .execute();
    }

    @Override
    public long deleteRentalStartDateBeforeThan(final LocalDate date) {
        return jpaQueryFactory.delete(inventory)
            .where(inventory.rentalPeriod.rentalStartDate.before(date))
            .execute();
    }
}
