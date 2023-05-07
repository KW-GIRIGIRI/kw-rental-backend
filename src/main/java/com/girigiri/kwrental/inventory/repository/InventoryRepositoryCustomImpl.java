package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static com.girigiri.kwrental.inventory.domain.QInventory.inventory;

public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public InventoryRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Inventory> findAllWithEquipment(final Long memberId) {
        return jpaQueryFactory.selectFrom(inventory)
                .join(inventory.equipment).fetchJoin()
                .where(inventory.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public int deleteAll(final Long memberId) {
        return (int) jpaQueryFactory.delete(inventory)
                .where(inventory.memberId.eq(memberId))
                .execute();
    }

    @Override
    public Optional<Inventory> findWithEquipmentById(final Long id) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(inventory)
                        .leftJoin(inventory.equipment).fetchJoin()
                        .where(inventory.id.eq(id))
                        .fetchOne()
        );
    }
}
