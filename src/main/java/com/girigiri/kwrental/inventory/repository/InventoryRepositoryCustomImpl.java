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

    // TODO: 2023/04/06 회원으로 필터링 해야 한다.
    @Override
    public List<Inventory> findAllWithEquipment() {
        return jpaQueryFactory.selectFrom(inventory)
                .join(inventory.equipment).fetchJoin()
                .fetch();
    }

    @Override
    public int deleteAll() {
        return (int) jpaQueryFactory.delete(inventory)
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
